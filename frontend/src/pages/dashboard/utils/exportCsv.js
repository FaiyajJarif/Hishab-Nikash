export function exportMonthlyCsv(summary, categories, snapshots) {
    const rows = [];
  
    rows.push(["Month Summary"]);
    rows.push(["Income", summary.income]);
    rows.push(["Expense", summary.expense]);
    rows.push(["Assigned", summary.assigned]);
    rows.push(["Unassigned", summary.unassigned]);
    rows.push([]);
  
    rows.push(["Category Breakdown"]);
    rows.push(["Category", "Amount"]);
    categories.forEach(c =>
      rows.push([c.categoryName, c.amount])
    );
    rows.push([]);
  
    rows.push(["Snapshot History"]);
    rows.push(["Month", "Income", "Expense", "Assigned"]);
    snapshots.forEach(s =>
      rows.push([
        `${s.month}/${s.year}`,
        s.income,
        s.expense,
        s.assigned
      ])
    );
  
    const csv = rows.map(r => r.join(",")).join("\n");
    download(csv, `monthly-analytics-${summary.month}-${summary.year}.csv`);
  }
  
  function download(text, filename) {
    const blob = new Blob([text], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
  