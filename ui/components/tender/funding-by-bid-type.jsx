import React from "react";
import Component from "../pure-render-component";
import {BarChart} from "react-d3-basic";

var chartSeries = [{
  field: 'totalTenderAmount',
  name: 'Total tender amount'
}];

export default class FundingByBidType extends Component{
  render(){
    var {data, width} = this.props;
    var maxValue = data.map(x => x.totalTenderAmount).reduce((a, b) => Math.max(a,b), 0);
    return (
        <section>
          <h4 className="page-header">Funding by bid type</h4>
          <BarChart
              data={data}
              width={width}
              height={350}
              chartSeries = {chartSeries}
              x= {x => x._id}
              xScale= {"ordinal"}
              yDomain={[0, maxValue]}
              margins={{top: 80, right: 100, bottom: 80, left: 200}}
          />
        </section>
    )
  }
}