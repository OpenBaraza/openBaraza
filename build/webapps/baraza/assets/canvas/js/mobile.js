
const topHalf = document.getElementById("top-half");
console.log(topHalf);
topHalf.addEventListener("touchstart", e => {
    // e.preventDefault(); // prevent touchstart
    console.log("Touches", e.touches.length)
    console.log("Targets", e.targetTouches.length) // number of fingers
    console.log("Changed", e.changedTouches.length)

    if(e.targetTouches.length >= 2) {
        alert("Touched more than 2 fingers")
    }
    alert("Touched Top Half")
 })

document.addEventListener("touchstart", e => {
    ;[...e.changedTouches].forEach(touch => {
        const dot = document.createElement("div")
        dot.classList.add("dot")
        dot.style.top = `${touch.pageY}px`
        dot.style.left = `${touch.pageX}px`
        dot.id = touch.identifier
        document.body.append(dot);
    })
})

document.addEventListener("touchmove", e => {
    ;[...e.changedTouches].forEach(touch => {
        const dot = document.getElementById(touch.identifier)
        dot.style.top = `${touch.pageY}px`
        dot.style.left = `${touch.pageX}px`
        // dot.id = touch.identifier
        // document.body.append(dot);
    })
})

document.addEventListener("touchend", e => {
    ;[...e.changedTouches].forEach(touch => {
        const dot = document.getElementById(touch.identifier)
        dot.remove()
    })
})

// if touched 

