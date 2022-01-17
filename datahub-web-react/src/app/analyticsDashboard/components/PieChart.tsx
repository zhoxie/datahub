import React, { useMemo } from 'react';
import { Pie } from '@vx/shape';
import { scaleOrdinal } from '@vx/scale';
import { Group } from '@vx/group';
import { defaultStyles, useTooltip, useTooltipInPortal } from '@visx/tooltip';
import Legend from './Legend';
import { lineColors } from './lineColors';
import { PieChart as PieChartType } from '../../../types.generated';

type Props = {
    chartData: PieChartType;
    width: number;
    height: number;
};

const MARGIN_SIZE = 32;

function transformChartData(chartData: PieChartType) {
    return chartData.pies.map((pie, i) => ({
        index: i,
        name: pie.name,
        ...pie.segment,
    }));
}

function PieArc({ arc, pie, totals, index }: { arc: any; pie: any; totals: any; index: number }) {
    const opacity = 1;
    const [centroidX, centroidY] = pie.path.centroid(arc);
    const { showTooltip, hideTooltip, tooltipOpen, tooltipData, tooltipTop, tooltipLeft } = useTooltip<string>();
    const { containerRef, TooltipInPortal } = useTooltipInPortal({
        scroll: true,
    });
    const getKey = (item: { [key: string]: any }) => {
        return `${((+item.value * 100) / totals).toFixed(2)} % `;
    };
    const tooltipStyles = {
        ...defaultStyles,
        minWidth: 150,
        backgroundColor: 'rgba(255,255,255,0.8)',
        color: '#555',
    };
    const handleMouseOver = (event, datum) => {
        showTooltip({
            tooltipLeft: centroidX,
            tooltipTop: centroidY + 100,
            tooltipData: datum,
        });
    };
    return (
        <g ref={containerRef}>
            <path
                d={pie.path(arc)}
                fill={lineColors[index]}
                fillOpacity={opacity}
                onMouseOver={(event) => {
                    handleMouseOver(event, arc);
                }}
                onMouseOut={hideTooltip}
            />
            <text fill="white" x={centroidX} y={centroidY} dy=".33em" fontSize={9} textAnchor="middle">
                {getKey(arc)}
            </text>
            {tooltipOpen && tooltipData && (
                <TooltipInPortal
                    top={tooltipTop}
                    left={tooltipLeft}
                    style={{ ...tooltipStyles, backgroundColor: lineColors[index] }}
                >
                    <div>
                        <strong>name: {arc?.data?.label} </strong>
                        <br />
                        <strong>value: {(+arc?.value)?.toFixed(2)} </strong>
                        <br />
                        <strong>total: {(+totals).toFixed(2)}</strong>
                    </div>
                </TooltipInPortal>
            )}
        </g>
    );
}

export const PieChart = ({ chartData, width, height }: Props) => {
    const keys = useMemo(
        () => chartData.pies.flatMap((bar) => bar.segment.label).filter((x, i, a) => a.indexOf(x) === i),
        [chartData],
    );
    const totals = useMemo(() => chartData.pies.reduce((total, pie) => total + pie.segment.value, 0), [chartData]);

    const segmentScale = scaleOrdinal<string, string>({
        domain: keys,
        range: lineColors.slice(0, keys.length),
    });

    const transformedChartData = useMemo(() => transformChartData(chartData), [chartData]);
    console.log('transformedChartData,', transformedChartData);

    const xMax = width - MARGIN_SIZE;
    const yMax = height;
    const margin = { left: 1, right: 1, top: 1, bottom: 1 };

    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;
    const radius = Math.min(innerWidth, innerHeight) * 0.6;
    const donutThickness = 50;

    return (
        <>
            <svg width={width + MARGIN_SIZE} height={height}>
                <rect x={0} y={0} width={width} height={height} fill="white" rx={14} />
                <Group top={xMax / 2} left={yMax / 2}>
                    <Pie
                        data={transformedChartData}
                        outerRadius={radius - donutThickness * 1.3}
                        pieSortValues={() => -1}
                        pieValue={(d: any) => d.value}
                    >
                        {(pie) => {
                            return pie.arcs?.map((arc: any, index: number) => {
                                return <PieArc pie={pie} arc={arc} totals={totals} index={index} />;
                            });
                        }}
                    </Pie>
                </Group>
            </svg>
            <Legend ordinalScale={segmentScale} />
        </>
    );
};
