import {Map} from "immutable";
import {pluck} from "../tools";
import Table from "../visualizations/tables/index";
import ReactDOMServer from "react-dom/server";
import CustomPopupChart from "./custom-popup-chart";

const pluckObj = (field, obj) => Object.keys(obj).map(key => obj[key][field]);

class CorruptionType extends CustomPopupChart{
  groupData(data){
    let grouped = {
			COLLUSION: {},
			FRAUD: {},
			RIGGING: {}
		};
    const {monthly} = this.props;

    data.forEach(datum => {
      const type = datum.get('type');
			let date;
			if(monthly){
				const month = datum.get('month');
				date = this.t(`general:months:${month}`);
			} else {
				date = datum.get('year');
			}
      grouped[type] = grouped[type] || {};
      grouped[type][date] = datum.toJS();
    });

    return grouped;
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
		const {styling} = this.props;
    const grouped = this.groupData(data);
    return Object.keys(grouped).map((type, index) => {
      const dataForType = grouped[type];
      return {
        x: Object.keys(dataForType),
        y: pluckObj('indicatorCount', dataForType),
        type: 'scatter',
        fill: 'tonexty',
        name: type,
        fillcolor: styling.charts.traceColors[index],
        line: {
          color: styling.charts.traceColors[index]
        }
      }
    });
  }

  getLayout(){
    return {
      hovermode: 'closest',
      xaxis: {
        type: 'category'
      },
      yaxis: {}
    }
  }

  getPopup(){
    const {popup} = this.state;
    const {year, traceName: corruptionType} = popup;
    const data = this.groupData(super.getData());
		if(!data[corruptionType]) return null;
    const dataForPoint = data[corruptionType][year];
		if(!dataForPoint) return null;
    return (
      <div className="crd-popup" style={{top: popup.top, left: popup.left}}>
        <div className="row">
          <div className="col-sm-12 info text-center">
            {year}
          </div>
          <div className="col-sm-12">
            <hr/>
          </div>
          <div className="col-sm-7 text-right title">Indicators</div>
          <div className="col-sm-5 text-left info">{dataForPoint.indicatorCount}</div>
          <div className="col-sm-7 text-right title">Flags</div>
          <div className="col-sm-5 text-left info">{dataForPoint.flaggedProjectCount}</div>
          <div className="col-sm-7 text-right title">Projects</div>
          <div className="col-sm-5 text-left info">{dataForPoint.projectCount}</div>
          <div className="col-sm-7 text-right title">% of Projects Flagged</div>
          <div className="col-sm-5 text-left info">{dataForPoint.percent.toFixed(2)}%</div>
        </div>
        <div className="arrow"/>
      </div>
    )
  }
}

CorruptionType.endpoint = 'percentTotalProjectsFlaggedByYear';

class TopFlaggedContracts extends Table{
  row(entry, index){
    const tenderValue = entry.getIn(['tender', 'value']);
    const awardValue = entry.getIn(['awards', 0, 'value']);
    const tenderPeriod = entry.get('tenderPeriod');
    const startDate = new Date(tenderPeriod.get('startDate'));
    const endDate = new Date(tenderPeriod.get('endDate'));
    const flaggedStats = entry.get('flaggedStats');
    return (
      <tr key={index}>
        <td></td>
        <td>{entry.get('ocid')}</td>
        <td>{entry.get('title')}</td>
        <td>{entry.getIn(['procuringEntity', 'name'])}</td>
        <td>{tenderValue.get('amount')} {tenderValue.get('currency')}</td>
        <td>{awardValue.get('amount')} {awardValue.get('currency')}</td>
        <td>{startDate.toLocaleDateString()}&mdash;{endDate.toLocaleDateString()}</td>
        <td>{flaggedStats.get('type')}</td>
        <td>{flaggedStats.get('count')}</td>
      </tr>
    )
  }

  render(){
    const {data} = this.props;
    return (
      <table className="table table-striped table-hover table-top-flagged-contracts">
        <thead>
          <tr>
            <th>Status</th>
            <th>Contract ID</th>
            <th>Title</th>
            <th>Procuring Entity</th>
            <th>Tender Amount</th>
            <th>Awards Amount</th>
            <th>Tender Date</th>
            <th className="flag-type">Flag Type</th>
            <th>Number of<br/>risk type flags</th>
          </tr>
        </thead>
        <tbody>
          {data && data.map(this.row.bind(this))}
        </tbody>
      </table>
    )
  }
}

TopFlaggedContracts.endpoint = 'corruptionRiskOverviewTable?pageSize=10';

class OverviewPage extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
      corruptionType: null,
      topFlaggedContracts: null
    }
  }

  render(){
    const {corruptionType, topFlaggedContracts} = this.state;
    const {filters, translations, years, monthly, months, styling, width} = this.props;
    return (
      <div className="page-overview">
        <section className="chart-corruption-types">
          <h4>Risk of Fraud, Collusion and Process Rigging Over Time</h4>
          <CorruptionType
              filters={filters}
              requestNewData={(_, corruptionType) => this.setState({corruptionType})}
              translations={translations}
              data={corruptionType}
              years={years}
							monthly={monthly}
							months={months}
              styling={styling}
							width={width}
          />
        </section>
        <section>
          <h4>The Projects with the Most Fraud, Collusion and Process Rigging Flags</h4>
          <TopFlaggedContracts
              filters={filters}
              data={topFlaggedContracts}
              translations={translations}
							years={years}
							monthly={monthly}
							months={months}
              requestNewData={(_, topFlaggedContracts) => this.setState({topFlaggedContracts})}
          />
        </section>
      </div>
    )
  }
}

export default OverviewPage;
