# Clarity Solution Bar Chart

> **Pedagogical purpose of this module**
>
> - Integrate a third-party library (Chart.js via react-chartjs-2) into a Liferay client extension.
> - Bundle this client extension with Vite.
> - Run the module locally, standalone, via `yarn dev`, using a stub that simulates both `window.Liferay.Util.fetch` and the paginated API normally served by Liferay (see [index.html](index.html)).
> - Illustrate how to handle client-side pagination of Liferay API calls (page size deliberately reduced to `2` so the progressive chart building is easy to see).
> - Illustrate how to make a custom element configurable through a fragment, thanks to the `jsImportMapsEntry` client extension type.

Liferay custom element (`clarity-solution-bar-chart`) that renders a bar chart (via [Chart.js](https://www.chartjs.org/) / [react-chartjs-2](https://react-chartjs-2.js.org/)) built from a live count aggregation over any Liferay REST endpoint (e.g. a custom Object's REST API) — no backend/aggregation code required, everything is fetched and aggregated client-side.

## Usage

```html
<clarity-solution-bar-chart
  title="Stores by tier"
  dataset-label="Count by tier"
  aggregation-field="tier.key"
  aggregation-type="count"
  rest-context-path="/o/c/stores"
  color="#4C9AFF"
></clarity-solution-bar-chart>
```

Attributes:

- `title` — chart title.
- `dataset-label` — label of the (single) dataset shown in the legend/tooltip.
- `aggregation-field` — field to group/count by, read from each item returned by the REST endpoint; supports dot paths (e.g. `tier.key` reads `item.tier.key`).
- `aggregation-type` — how values are aggregated. Only `"count"` is implemented today (see [src/components/LiferayChart.jsx](src/components/LiferayChart.jsx)); other types (sums, averages, ...) are not handled yet.
- `rest-context-path` — the Liferay REST endpoint to fetch items from (e.g. `/o/c/myobjects`).
- `color` — bar fill color.

## How it works

- [src/index.jsx](src/index.jsx) defines the custom element and mounts the React app directly on the element itself (no shadow DOM here), forwarding its attributes as props.
- [src/App.jsx](src/App.jsx) passes those props down to `LiferayChart`.
- [src/components/LiferayChart.jsx](src/components/LiferayChart.jsx) paginates through `restContextPath` with `window.Liferay.Util.fetch`, requesting only `aggregationField` (`?fields=...&page=...&pageSize=...`), counts occurrences per page and merges them into a running total, then re-renders the chart after every page — so the chart progressively fills in while older pages are being fetched — until `lastPage` is reached.
- [src/components/BarChart.jsx](src/components/BarChart.jsx) renders the resulting `labels`/`values` as a Chart.js bar chart.

### Progressive chart building

Liferay REST APIs are paginated by design — a single call never returns the whole collection, only one page plus a `lastPage` indicator. `LiferayChart.jsx` embraces that instead of working around it: rather than waiting to fetch every page before drawing anything, it counts and re-renders after **each** page (`fetchData` calls itself for `page + 1` once a page's results are merged in, until `page >= lastPage`), so the bars appear and grow continuously as pages come in instead of popping in all at once at the end.

> **Note:** `pageSize` is hardcoded to `2` in `LiferayChart.jsx`. That's deliberately tiny — a real deployment would use a much larger page size — chosen here purely so the page-by-page construction of the chart is easy to see with a handful of demo records instead of resolving in a single, instantaneous fetch.

## Fragment

[fragments/vertical-bar-chart](fragments/vertical-bar-chart) packages the element as a Liferay page fragment so editors can configure `title`, `restContextPath`, `aggregationField`, `datasetLabel` and the bar color (`chartColor`, a color-palette field) from the page/fragment editor instead of hand-writing HTML — see [configuration.json](fragments/vertical-bar-chart/configuration.json) and [fragment.html](fragments/vertical-bar-chart/fragment.html).

## Development

```bash
yarn            # install dependencies
yarn dev        # Vite dev server (index.html), standalone with the mocked Liferay runtime
yarn build      # production build → build/vite
```

`yarn dev` above runs the element fully standalone against the stub described in the pedagogical note (no running Liferay portal needed). To instead run against a real Liferay instance and get hot reload while editing, deploy the client extension, start `yarn dev`, and let [client-extension.dev.yaml](client-extension.dev.yaml) proxy the module's URL to the local Vite dev server (`http://localhost:5173`).

The build is packaged as a Liferay JS import map client extension (`jsImportMapsEntry`, bare specifier `clarity-solution-bar-chart`) by [client-extension.yaml](client-extension.yaml).