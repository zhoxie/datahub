package com.linkedin.metadata.resources.lineage;

import com.codahale.metrics.MetricRegistry;
import com.linkedin.common.EntityRelationship;

import com.linkedin.common.EntityRelationshipArray;
import com.linkedin.common.EntityRelationships;
import com.linkedin.common.urn.Urn;
import com.linkedin.metadata.graph.RelatedEntitiesResult;
import com.linkedin.metadata.graph.GraphService;
import com.linkedin.metadata.query.CriterionArray;
import com.linkedin.metadata.query.Filter;
import com.linkedin.metadata.query.RelationshipDirection;
import com.linkedin.metadata.restli.RestliUtil;
import com.linkedin.parseq.Task;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiSimpleResource;
import com.linkedin.restli.server.annotations.RestMethod;
import com.linkedin.restli.server.resources.SimpleResourceTemplate;

import io.opentelemetry.extension.annotations.WithSpan;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.linkedin.metadata.dao.Neo4jUtil.*;
import static com.linkedin.metadata.dao.utils.QueryUtils.newFilter;


/**
 * Rest.li entry point: /relationships?type={entityType}&direction={direction}&types={types}
 */
@RestLiSimpleResource(name = "relationships", namespace = "com.linkedin.lineage")
public final class Relationships extends SimpleResourceTemplate<EntityRelationships> {

    private static final Filter EMPTY_FILTER = new Filter().setCriteria(new CriterionArray());
    private static final Integer MAX_DOWNSTREAM_CNT = 100;

    @Inject
    @Named("graphService")
    private GraphService _graphService;

    public Relationships() {
        super();
    }

    private RelatedEntitiesResult getRelatedEntities(
        String rawUrn,
        List<String> relationshipTypes,
        RelationshipDirection direction,
        @Nullable Integer start,
        @Nullable Integer count) {

        start = start == null ? 0 : start;
        count = count == null ? MAX_DOWNSTREAM_CNT : count;

        return _graphService.findRelatedEntities("", newFilter("urn", rawUrn),
            "", EMPTY_FILTER,
            relationshipTypes, createRelationshipFilter(EMPTY_FILTER, direction),
            start, count);
    }

    static RelationshipDirection getOppositeDirection(RelationshipDirection direction) {
        if (direction.equals(RelationshipDirection.INCOMING)) {
            return RelationshipDirection.OUTGOING;
        }
        if (direction.equals(RelationshipDirection.OUTGOING)) {
            return RelationshipDirection.INCOMING;
        }
        return direction;
    }

    @Nonnull
    @RestMethod.Get
    @WithSpan
    public Task<EntityRelationships> get(
            @QueryParam("urn") @Nonnull String rawUrn,
            @QueryParam("types") @Nonnull String[] relationshipTypesParam,
            @QueryParam("direction") @Nonnull String rawDirection,
            @QueryParam("start") @Optional @Nullable Integer start,
            @QueryParam("count") @Optional @Nullable Integer count
    ) {
        RelationshipDirection direction = RelationshipDirection.valueOf(rawDirection);
        final List<String> relationshipTypes = Arrays.asList(relationshipTypesParam);
        return RestliUtil.toTask(() -> {

            final RelatedEntitiesResult relatedEntitiesResult = getRelatedEntities(
                rawUrn,
                relationshipTypes,
                direction,
                start,
                count);
            final EntityRelationshipArray entityArray = new EntityRelationshipArray(
                    relatedEntitiesResult.getEntities().stream().map(
                        entity -> {
                            try {
                                return new EntityRelationship()
                                    .setEntity(Urn.createFromString(entity.getUrn()))
                                    .setType(entity.getRelationshipType());
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(
                                    String.format("Failed to convert urnStr %s found in the Graph to an Urn object", entity.getUrn()));
                            }
                        }
                    ).collect(Collectors.toList())
            );

            return new EntityRelationships()
                .setStart(relatedEntitiesResult.getStart())
                .setCount(relatedEntitiesResult.getCount())
                .setTotal(relatedEntitiesResult.getTotal())
                .setRelationships(entityArray);
        }, MetricRegistry.name(this.getClass(), "getLineage"));
    }

    @Nonnull
    @RestMethod.Delete
    public UpdateResponse delete(
            @QueryParam("urn") @Nonnull String rawUrn
    ) throws Exception {
        _graphService.removeNode(Urn.createFromString(rawUrn));
        return new UpdateResponse(HttpStatus.S_200_OK);
    }
}
