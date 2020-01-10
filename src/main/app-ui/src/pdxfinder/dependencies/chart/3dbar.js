
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
        "Attendance": 271,
        "Month": "March",
        "Teachers": 100,
        "Converts": 30,
        "Visitors": 31,
        "url": "#"
    }, {
        "Attendance": 34,
        "Month": "April",
        "Teachers": 38,
        "Converts": 32,
        "Visitors": 28,
        "url": "#"
    }, {"Attendance": 68, "Month": "June", "Teachers": 124, "Converts": 64, "Visitors": 71, "url": "#"}],
    "valueAxes": [{"position": "left", "title": "Summary This Year"}],
    "graphs": [{
        "balloonText": "<span style='font-size:10px;'>Attendance: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Attendance",
        "urlField": "url",
        "fillColors": "#dc3545"
    }, {
        "balloonText": "<span style='font-size:10px;'>Teachers: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Teachers",
        "urlField": "url",
        "fillColors": "#17a2b8"
    }, {
        "balloonText": "<span style='font-size:10px;'>Converts: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Converts",
        "urlField": "url",
        "fillColors": "#007bff"
    }, {
        "balloonText": "<span style='font-size:10px;'>Visitors: [[value]]<\/span>",
        "fillAlphas": 0.8,
        "lineAlpha": 0.2,
        "type": "column",
        "valueField": "Visitors",
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
