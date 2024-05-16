'use client';

import Title1 from "@/components/title1";

import { ResponsiveChartContainer } from '@mui/x-charts/ResponsiveChartContainer';
import { LinePlot, MarkPlot } from '@mui/x-charts/LineChart';
import { BarPlot } from '@mui/x-charts/BarChart';
import { ChartsXAxis } from '@mui/x-charts/ChartsXAxis';
import { ChartsYAxis } from '@mui/x-charts/ChartsYAxis';
import { ChartsGrid } from '@mui/x-charts/ChartsGrid';
import { ChartsTooltip } from '@mui/x-charts/ChartsTooltip';
import { LineSeriesType, type AllSeriesType, type BarSeriesType } from '@mui/x-charts';
import type { DatasetType } from "node_modules/@mui/x-charts/models/seriesType/config";
import chroma from 'chroma-js';
import { useEffect, useState } from "react";
import Select from "@/components/select";
import { getUserControllerApi } from "@/openapi/connector";

enum categorySelections {
  PARTICIPANT = "PARTICIPANT",
  SOCIAL_WORKER = "SOCIAL_WORKER",
  ALL = "ALL"
}

const getNextColor = (level: number) => {
  return chroma.hex("#5eead5").darken(level * 0.5).hex();
}

export default function OverviewPage() {
  const [categorySelection, setCategorySelection] = useState<any>(categorySelections.PARTICIPANT);
  const [participant, setParticipant] = useState<any>();
  const [month, setMonth] = useState<string>(new Date().toLocaleString('en-US', { month: '2-digit' }).padStart(2, '0'));
  const [year, setYear] = useState<string>(new Date().getFullYear().toString());
  
  const [categories, setCategories] = useState<{ id: string, label: string }[]>([]);
  const [participants, setParticipants] = useState<{ id: string, label: string }[]>([]);
  const [months, setMonths] = useState<{ id: string, label: string }[]>([]);
  const [years, setYears] = useState<{ id: string, label: string }[]>([]);

  const [incidentsSeries, setIncidentsSeries] = useState<BarSeriesType[]>([]);
  const [dataSeries, setDataSeries] = useState<AllSeriesType[]>([]);

  const [incidentsDataSet, setIncidentsDataSet] = useState<DatasetType>([]);
  const [dataset, setDataset] = useState<DatasetType>([]);

  useEffect(() => {
    setCategories([
      { id: categorySelections.PARTICIPANT, label: 'Teilnehmerkategorien' },
      { id: categorySelections.SOCIAL_WORKER, label: 'Sozialarbeiterkategorien' },
      { id: categorySelections.ALL, label: 'Alle Kategorien' }
    ])

    setMonths([
      { id: '01', label: 'Januar' },
      { id: '02', label: 'Februar' },
      { id: '03', label: 'März' },
      { id: '04', label: 'April' },
      { id: '05', label: 'Mai' },
      { id: '06', label: 'Juni' },
      { id: '07', label: 'Juli' },
      { id: '08', label: 'August' },
      { id: '09', label: 'September' },
      { id: '10', label: 'Oktober' },
      { id: '11', label: 'November' },
      { id: '12', label: 'Dezember' }
    ]);

    const yearsList = [];
    for (let i = 2024; i <= new Date().getFullYear(); i++) {
      yearsList.push({ id: i.toString(), label: i.toString() });
    }
    setYears(yearsList);

    getUserControllerApi().getAllParticipants().then(participants => {
      setParticipants(participants.map(participant => participant && ({ 
        id: participant.userId ?? "",
        label: participant.email ?? ""
      })) ?? []);
      participants[0] && setParticipant(participants[0]);
    });
  }, []);

  useEffect(() => {
    if (participant) {
      setIncidentsSeries([
        { type: 'bar', dataKey: 'count', color: '#134e4a', label: 'Vorfälle' },
      ]);

      setDataSeries([
        { type: 'line', dataKey: 'min', color: '#000', label: "Arbeitszeit" },
        { type: 'bar', dataKey: 'precip1', color: getNextColor(0), yAxisKey: 'rightAxis', label: "Stimmungskategorie 1" },
        { type: 'bar', dataKey: 'precip2', color: getNextColor(1), yAxisKey: 'rightAxis', label: "Stimmungskategorie 2" },
        { type: 'bar', dataKey: 'precip3', color: getNextColor(2), yAxisKey: 'rightAxis', label: "Stimmungskategorie 3" },
      ]);

      const incidents = [];
      const data = [];

      for (let i = 0; i < 30; i++) {
        incidents.push({
          day: `${(i + 1)}. Mai`,
          count: Math.round(Math.random() * 4),
        });
      }

      for (let i = 0; i < 30; i++) {
        data.push({
          day: `${(i + 1)}. Mai`,
          min: Math.random() * 4 + 4,
          precip1: Math.random() * 100,
          precip2: Math.random() * 100,
          precip3: Math.random() * 100,
          precip4: Math.random() * 100,
          precip5: Math.random() * 100,
        });
      }

      setIncidentsDataSet(incidents);
      setDataset(data);
    }
  }, [categorySelection, participant, month, year]);

  return (
    <>
      <div className="h-full w-full flex flex-col">
        <div className="flex flex-col xl:flex-row justify-between">
          <Title1>Monatsübersicht</Title1>
          <div className="mt-2 sm:mt-0">
            <Select
              className="w-32 inline-block mr-4 mb-4"
              placeholder="Monat"
              data={months}
              value={month}
              onChange={(e) => setMonth(e.target.value)} />
            <Select
              className="w-24 inline-block mr-4 mb-4"
              placeholder="Jahr"
              data={years}
              value={year}
              onChange={(e) => setYear(e.target.value)} />
            <Select
              className="w-48 inline-block mr-4 mb-4"
              placeholder="Kategorien"
              data={categories}
              value={categorySelection}
              onChange={(e) => setCategorySelection(e.target.value)} />
            <Select
              className="w-40 inline-block mb-4"
              placeholder="Teilnehmer"
              data={participants}
              value={participant}
              onChange={(e) => setParticipant(e.target.value)} />
          </div>
        </div>
    
        <div className="h-full overflow-x-auto flex flex-col space-y-4">
          <div className="min-w-[2200px] bg-white rounded-xl h-36">
            <ResponsiveChartContainer
              series={incidentsSeries as unknown as LineSeriesType[]}
              xAxis={[
                {
                  scaleType: 'band',
                  dataKey: 'day',
                },
              ]}
              yAxis={[
                { id: 'leftAxis' },
              ]}
              dataset={incidentsDataSet}
            >
              <ChartsGrid horizontal />
              <BarPlot />
              <LinePlot />
              <ChartsXAxis />
              <ChartsYAxis axisId="leftAxis" label="Vorfälle" />
              <ChartsTooltip trigger="item" faded="global"/>
            </ResponsiveChartContainer>
          </div>
            
          <div className="min-w-[2200px] bg-white rounded-xl grow">
            <ResponsiveChartContainer
              series={dataSeries as unknown as LineSeriesType[]}
              xAxis={[
                {
                  scaleType: 'band',
                  dataKey: 'day',
                },
              ]}
              yAxis={[
                { id: 'leftAxis' },
                { id: 'rightAxis' }
              ]}
              dataset={dataset}
            >
              <ChartsGrid horizontal />
              <BarPlot />
              <LinePlot />
              <MarkPlot /> 
              <ChartsXAxis />
              <ChartsYAxis axisId="leftAxis" label="Arbeitszeit (in h)" />
              <ChartsYAxis
                axisId="rightAxis"
                position="right"
                label="Stimmungskategorien (in %)" 
              />
              <ChartsTooltip />
            </ResponsiveChartContainer>
          </div>
        </div>
      </div>
    </>
  );
};