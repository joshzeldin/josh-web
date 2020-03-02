d3.selectAll("circle").style("opacity", 0.0)

d3.select("svg").call(d3.zoom().on("zoom", function () {
    d3.select("g.map").attr("transform", d3.event.transform)
 }))

var trekScroll = window.requestAnimationFrame ||
    function(callback){
        window.setTimeout(callback, 1000/60)
    };

function arrivedAtLocation() {
    cities = d3.selectAll("g.trek").selectAll("circle")
    cities.style("opacity", function(d, i) {
        scrollPercent = getScrollPercent();
        trekDistance = i / cities.size();
        return trekDistance > scrollPercent ? 0.0 : 1.0;
        });
    trekScroll(arrivedAtLocation)
}
arrivedAtLocation()