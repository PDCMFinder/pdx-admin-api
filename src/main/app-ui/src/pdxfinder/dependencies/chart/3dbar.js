// function pdxFinderbarChart(title, chartData, cssID, categoryField, valueField, labelRotation) {
//
//
//     AmCharts.addInitHandler(function(chart) {
//         // check if there are graphs with autoColor: true set
//         for(var i = 0; i < chart.graphs.length; i++) {
//             var graph = chart.graphs[i];
//             if (graph.autoColor !== true)
//                 continue;
//             var colorKey = "autoColor-"+i;
//             graph.lineColorField = colorKey;
//             graph.fillColorsField = colorKey;
//             for(var x = 0; x < chart.dataProvider.length; x++) {
//                 var color = chart.colors[x]
//                 chart.dataProvider[x][colorKey] = color;
//             }
//         }
//
//     }, ["serial"]);
//
//
//      var chart = AmCharts.makeChart(cssID, {
//         "theme": "light",
//         "type": "serial",
//         "startDuration": 2,
//         "dataProvider": chartData,
//         "valueAxes": [{
//             "position": "left",
//             "title": title
//         }],
//         "graphs": [{
//             "balloonText": "[[category]]: <b>[[value]]</b>",
//             "fillColorsField": "color",
//             "fillAlphas": 1,
//             "lineAlpha": 0.1,
//             "type": "column",
//             "valueField": valueField,
//             "showHandOnHover": true,
//             "autoColor": true
//         }],
//         "depth3D": 20,
//         "angle": 30,
//         "chartCursor": {
//             "categoryBalloonEnabled": true,
//             "cursorAlpha": 0,
//             "zoomable": true
//         },
//         "categoryField": categoryField,
//         "categoryAxis": {
//             "gridPosition": "start",
//             "labelRotation": labelRotation,
//             "labelFunction": function(valueText, serialDataItem, categoryAxis) {
//                 if (valueText.length > 10)
//                     return valueText.substring(0, 10) + '...';
//                 else
//                     return valueText;
//             }
//         },
//         "listeners": [{
//             "event": "clickGraphItem",
//             "method": function(event) {
//                 var drillDownLink = '/diagnosis-mapping/'+event.item.category;
//                 window.open(drillDownLink,'_parent');
//             }
//         }],
//         "export": {
//             "enabled": true
//         }
//
//
//     });
//
//
//
// }


var chart = AmCharts.makeChart("chartdiv", {
    "theme": "light",
    "type": "serial",
    "startDuration": 2,
    "dataProvider": [{
        "Mutation": 808,
        "Month": "April 2018",
        "Dosing Studies": 356,
        "url": "#"
    }, {
        "Mutation": 1472,
        "Month": "April 2019",
        "Cytogenetics": 81,
        "Copy Number Alteration": 729,
        "Dosing Studies": 371,
        "Patient Treatment": 216,
        "url": "#"
    }, {
        "Mutation": 1546,
        "Month": "October 2019",
        "Cytogenetics": 128,
        "Copy Number Alteration": 1082,
        "Dosing Studies": 370,
        "Patient Treatment": 244,
        "url": "#"
    }],
    "valueAxes": [{"position": "left", "title": "Data Available / Data Release"}],
    "graphs": [{
        "balloonText": "<span style='font-size:10px;'>Mutation: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Mutation",
        "urlField": "url",
        "fillColors": "#dc3545"
    }, {
        "balloonText": "<span style='font-size:10px;'>Cytogenetics: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Cytogenetics",
        "urlField": "url",
        "fillColors": "#17a2b8"
    }, {
        "balloonText": "<span style='font-size:10px;'>CNA: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Copy Number Alteration",
        "urlField": "url",
        "fillColors": "#007bff"
    }, {
        "balloonText": "<span style='font-size:10px;'>Dosing Studies: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Dosing Studies",
        "urlField": "url",
        "fillColors": "#04478F"
    }, {
        "balloonText": "<span style='font-size:10px;'>Patient Treatment: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Patient Treatment",
        "urlField": "url",
        "fillColors": "#04478F"
    }],
    "depth3D": 30,
    "angle": 30,
    "chartCursor": {"categoryBalloonEnabled": true, "cursorAlpha": 0, "zoomable": true},
    "categoryField": "Month",
    "categoryAxis": {"gridPosition": "start", "labelRotation": 5}
});

chart.addListener("clickGraphItem", handleClick)

function handleClick(event) {
    var balloonText = event.item.graph.balloonText;
    var drillDownLink = '/bi/' + balloonText.split(">")[1].split(":")[0].toLowerCase();
    ;
    window.open(drillDownLink, '_blank');
}
