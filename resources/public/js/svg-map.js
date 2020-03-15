d3.selectAll("circle").style("opacity", 0.0)

zoomBaseElement = d3.select("svg")

zoomBaseElement.call(d3.zoom().on("zoom", function () {
    console.log(d3.event.transform)
    d3.select("g.map").attr("transform", d3.event.transform)
 }))

var trekScroll = window.requestAnimationFrame ||
    function(callback){
        window.setTimeout(callback, 1000/60)
    };

function arrivedAtLocation() {
    cities = d3.selectAll("circle.trek.village")
    cities.style("opacity", function(d, i) {
        //console.log(d)
        scrollPercent = getScrollPercent();
        trekDistance = (i + 1)/ cities.size();
        return trekDistance > scrollPercent ? 0.0 : 1.0;
        });
    
    //d3.select("g.map").attr("transform", "translate(0,0)")
    trekScroll(arrivedAtLocation)
}
arrivedAtLocation()