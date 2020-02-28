var scroll = window.requestAnimationFrame ||
    function(callback){
        window.setTimeout(callback, 1000/60)
    };

var progress_bar = document.getElementById('progress-bar')

function getScrollPercent() {
    var h = document.documentElement, 
        b = document.body,
        st = 'scrollTop',
        sh = 'scrollHeight';
    return (h[st]||b[st]) / ((h[sh]||b[sh]) - h.clientHeight);
}


function loop() {
    progress_bar.style.width = getScrollPercent() * window.innerWidth + "px"
    scroll(loop)
}
loop()
