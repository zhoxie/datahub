import {
    dataset3,
    dataset3WithLineage,
    dataset4,
    dataset4WithLineage,
    dataset5,
    dataset5WithLineage,
    dataset6WithLineage,
} from '../../../Mocks';
import { EntityType } from '../../../types.generated';
import { getTestEntityRegistry } from '../../../utils/test-utils/TestPageContainer';
import { Direction, FetchedEntities } from '../types';
import constructTree from '../utils/constructTree';
import extendAsyncEntities from '../utils/extendAsyncEntities';

const testEntityRegistry = getTestEntityRegistry();

describe('constructTree', () => {
    it('handles nodes without any lineage', () => {
        const mockFetchedEntities = {};
        expect(
            constructTree(
                { entity: dataset3, type: EntityType.Dataset },
                mockFetchedEntities,
                Direction.Upstream,
                testEntityRegistry,
            ),
        ).toEqual({
            name: 'Yet Another Dataset',
            urn: 'urn:li:dataset:3',
            type: EntityType.Dataset,
            unexploredChildren: 0,
            children: [],
            icon: undefined,
            platform: 'Kafka',
        });
    });

    it('handles nodes with downstream lineage', () => {
        const fetchedEntities = [
            { entity: dataset4, direction: Direction.Upstream, fullyFetched: false },
            { entity: dataset5, direction: Direction.Upstream, fullyFetched: false },
        ];
        const mockFetchedEntities = fetchedEntities.reduce(
            (acc, entry) =>
                extendAsyncEntities(
                    acc,
                    testEntityRegistry,
                    { entity: entry.entity, type: EntityType.Dataset },
                    entry.fullyFetched,
                ),
            {} as FetchedEntities,
        );

        expect(
            constructTree(
                { entity: dataset6WithLineage, type: EntityType.Dataset },
                mockFetchedEntities,
                Direction.Downstream,
                testEntityRegistry,
            ),
        ).toEqual({
            name: 'Sixth Test Dataset',
            urn: 'urn:li:dataset:6',
            type: EntityType.Dataset,
            unexploredChildren: 0,
            icon: undefined,
            platform: 'Kafka',
            children: [
                {
                    name: 'Fourth Test Dataset',
                    type: EntityType.Dataset,
                    unexploredChildren: 0,
                    urn: 'urn:li:dataset:4',
                    countercurrentChildrenUrns: [],
                    children: [],
                    icon: undefined,
                    platform: 'Kafka',
                },
            ],
        });
    });

    it('handles nodes with upstream lineage', () => {
        const fetchedEntities = [
            { entity: dataset4, direction: Direction.Upstream, fullyFetched: false },
            { entity: dataset5, direction: Direction.Upstream, fullyFetched: false },
        ];
        const mockFetchedEntities = fetchedEntities.reduce(
            (acc, entry) =>
                extendAsyncEntities(
                    acc,
                    testEntityRegistry,
                    { entity: entry.entity, type: EntityType.Dataset },
                    entry.fullyFetched,
                ),
            {} as FetchedEntities,
        );

        expect(
            constructTree(
                { entity: dataset6WithLineage, type: EntityType.Dataset },
                mockFetchedEntities,
                Direction.Upstream,
                testEntityRegistry,
            ),
        ).toEqual({
            name: 'Sixth Test Dataset',
            urn: 'urn:li:dataset:6',
            type: EntityType.Dataset,
            unexploredChildren: 0,
            icon: undefined,
            platform: 'Kafka',
            children: [
                {
                    countercurrentChildrenUrns: [],
                    name: 'Fifth Test Dataset',
                    type: EntityType.Dataset,
                    unexploredChildren: 0,
                    urn: 'urn:li:dataset:5',
                    children: [],
                    icon: undefined,
                    platform: 'Kafka',
                },
            ],
        });
    });

    it('handles nodes with layers of lineage', () => {
        const fetchedEntities = [
            { entity: dataset4WithLineage, direction: Direction.Upstream, fullyFetched: true },
            { entity: dataset5WithLineage, direction: Direction.Upstream, fullyFetched: true },
            { entity: dataset6WithLineage, direction: Direction.Upstream, fullyFetched: true },
        ];
        const mockFetchedEntities = fetchedEntities.reduce(
            (acc, entry) =>
                extendAsyncEntities(
                    acc,
                    testEntityRegistry,
                    { entity: entry.entity, type: EntityType.Dataset },
                    entry.fullyFetched,
                ),
            {} as FetchedEntities,
        );

        expect(
            constructTree(
                { entity: dataset3WithLineage, type: EntityType.Dataset },
                mockFetchedEntities,
                Direction.Upstream,
                testEntityRegistry,
            ),
        ).toEqual({
            name: 'Yet Another Dataset',
            urn: 'urn:li:dataset:3',
            type: EntityType.Dataset,
            unexploredChildren: 0,
            icon: undefined,
            platform: 'Kafka',
            children: [
                {
                    name: 'Fourth Test Dataset',
                    type: EntityType.Dataset,
                    unexploredChildren: 0,
                    urn: 'urn:li:dataset:4',
                    countercurrentChildrenUrns: ['urn:li:dataset:3'],
                    icon: undefined,
                    platform: 'Kafka',
                    children: [
                        {
                            name: 'Sixth Test Dataset',
                            type: 'DATASET',
                            unexploredChildren: 0,
                            urn: 'urn:li:dataset:6',
                            countercurrentChildrenUrns: ['urn:li:dataset:4'],
                            icon: undefined,
                            platform: 'Kafka',
                            children: [
                                {
                                    name: 'Fifth Test Dataset',
                                    type: EntityType.Dataset,
                                    unexploredChildren: 0,
                                    urn: 'urn:li:dataset:5',
                                    children: [],
                                    countercurrentChildrenUrns: [
                                        'urn:li:dataset:7',
                                        'urn:li:dataset:6',
                                        'urn:li:dataset:4',
                                    ],
                                    icon: undefined,
                                    platform: 'Kafka',
                                },
                            ],
                        },
                        {
                            name: 'Fifth Test Dataset',
                            type: EntityType.Dataset,
                            unexploredChildren: 0,
                            urn: 'urn:li:dataset:5',
                            children: [],
                            countercurrentChildrenUrns: ['urn:li:dataset:7', 'urn:li:dataset:6', 'urn:li:dataset:4'],
                            icon: undefined,
                            platform: 'Kafka',
                        },
                    ],
                },
            ],
        });
    });

    it('for a set of identical nodes, both will be referentially identical', () => {
        const fetchedEntities = [
            { entity: dataset4WithLineage, direction: Direction.Upstream, fullyFetched: true },
            { entity: dataset5WithLineage, direction: Direction.Upstream, fullyFetched: true },
            { entity: dataset6WithLineage, direction: Direction.Upstream, fullyFetched: true },
        ];
        const mockFetchedEntities = fetchedEntities.reduce(
            (acc, entry) =>
                extendAsyncEntities(
                    acc,
                    testEntityRegistry,
                    { entity: entry.entity, type: EntityType.Dataset },
                    entry.fullyFetched,
                ),
            {} as FetchedEntities,
        );

        const tree = constructTree(
            { entity: dataset3WithLineage, type: EntityType.Dataset },
            mockFetchedEntities,
            Direction.Upstream,
            testEntityRegistry,
        );

        const fifthDatasetIntance1 = tree?.children?.[0]?.children?.[1];
        const fifthDatasetIntance2 = tree?.children?.[0]?.children?.[0]?.children?.[0];

        expect(fifthDatasetIntance1?.name).toEqual('Fifth Test Dataset');
        expect(fifthDatasetIntance2?.name).toEqual('Fifth Test Dataset');
        expect(fifthDatasetIntance1 === fifthDatasetIntance2).toEqual(true);
    });

    it('handles partially fetched graph with layers of lineage', () => {
        const fetchedEntities = [{ entity: dataset4WithLineage, direction: Direction.Upstream, fullyFetched: false }];
        const mockFetchedEntities = fetchedEntities.reduce(
            (acc, entry) =>
                extendAsyncEntities(
                    acc,
                    testEntityRegistry,
                    { entity: entry.entity, type: EntityType.Dataset },
                    entry.fullyFetched,
                ),
            {} as FetchedEntities,
        );
        expect(
            constructTree(
                { entity: dataset3WithLineage, type: EntityType.Dataset },
                mockFetchedEntities,
                Direction.Upstream,
                testEntityRegistry,
            ),
        ).toEqual({
            name: 'Yet Another Dataset',
            urn: 'urn:li:dataset:3',
            type: EntityType.Dataset,
            unexploredChildren: 0,
            icon: undefined,
            platform: 'Kafka',
            children: [
                {
                    name: 'Fourth Test Dataset',
                    type: EntityType.Dataset,
                    unexploredChildren: 2,
                    urn: 'urn:li:dataset:4',
                    children: [],
                    countercurrentChildrenUrns: ['urn:li:dataset:3'],
                    icon: undefined,
                    platform: 'Kafka',
                },
            ],
        });
    });
});
