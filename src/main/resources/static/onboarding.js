document.addEventListener("DOMContentLoaded", () => {

    /* ---------------- Elements ---------------- */

    const slides = document.querySelectorAll(".slide");
    const nextButtons = document.querySelectorAll(".next");
    const finishButton = document.querySelector(".finish");
    const progressBar = document.getElementById("progress");

    let currentStep = 0;
    const totalSteps = slides.length;
    const token = localStorage.getItem("auth_token");

    if (!token) {
        window.location.href = "/login";
        return;
    }

    /* ---------------- UI Helpers ---------------- */

    function showSlide(step) {
        slides.forEach((slide, index) => {
            slide.classList.toggle("active", index === step);
        });
        updateProgress(step);
    }

    function updateProgress(step) {
        const percent = Math.round(((step + 1) / totalSteps) * 100);
        progressBar.style.width = percent + "%";
    }

    /* ---------------- Build selections ---------------- */

    function buildSelections(slide, stepIndex) {

        // STEP_0 → Display Name
        if (stepIndex === 0) {
            const input = slide.querySelector("input");
            const name = input?.value.trim();
            if (!name) return [];
            return [{
                category: "display_name",
                value: name,
                frequency: null
            }];
        }
    
        // Other steps → semantic categories
        const options = slide.querySelector(".options");
        const category = options?.dataset.category;
    
        const selectedButtons =
            slide.querySelectorAll(".options button.selected");
    
        return [...selectedButtons].map(btn => ({
            category: category,        // 🔥 THIS FIXES EVERYTHING
            value: btn.innerText.trim(),
            frequency: null
        }));
    }    

    /* ---------------- Save step ---------------- */

    async function saveStep(stepIndex, selections) {
        await fetch("/api/onboarding/step", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                step: "STEP_" + stepIndex,
                selections: selections
            })
        });
    }

    /* ---------------- Button selection logic ---------------- */

    document.querySelectorAll(".options button").forEach(button => {
        button.addEventListener("click", () => {
            const parent = button.closest(".options");

            // Single-select
            if (!parent.classList.contains("multi")) {
                parent.querySelectorAll("button")
                      .forEach(b => b.classList.remove("selected"));
            }

            button.classList.toggle("selected");
        });
    });

    /* ---------------- Next buttons ---------------- */

    nextButtons.forEach(button => {
        button.addEventListener("click", async () => {

            const slide = slides[currentStep];
            const selections = buildSelections(slide, currentStep);

            if (!selections.length) {
                alert("Please answer before continuing.");
                return;
            }

            await saveStep(currentStep, selections);

            currentStep++;
            if (currentStep < totalSteps) {
                showSlide(currentStep);
            }
        });
    });

    /* ---------------- Finish ---------------- */

    if (finishButton) {
        finishButton.addEventListener("click", async () => {
    
            await fetch("/api/onboarding/step", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + localStorage.getItem("auth_token")
                },
                body: JSON.stringify({
                    step: "STEP_3",
                    selections: [] // or lifestyle selections already sent
                })
            });
            
            window.location.href = "/dashboard";
            
        });
    }    

    /* ---------------- Resume onboarding ---------------- */

    async function loadOnboardingStatus() {
        const res = await fetch("/api/onboarding/status", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        const data = await res.json();

        if (data.completed) {
            window.location.href = "/dashboard";
            return;
        }

        if (data.lastStep) {
            const stepNum = parseInt(data.lastStep.replace("STEP_", ""), 10);
            currentStep = Math.max(stepNum, 0);
        }        
    }

    /* ---------------- Init ---------------- */

    (async function init() {
        await loadOnboardingStatus();
        showSlide(currentStep);
    })();

});
