import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

export function exportMonthlyPdf(summary, categories, snapshots) {
  const doc = new jsPDF();

  doc.setFontSize(16);
  doc.text(
    `Monthly Analytics – ${summary.month}/${summary.year}`,
    14,
    18
  );

  doc.setFontSize(11);
  doc.text(`Income: ৳${summary.income}`, 14, 30);
  doc.text(`Expense: ৳${summary.expense}`, 14, 38);
  doc.text(`Assigned: ৳${summary.assigned}`, 14, 46);
  doc.text(`Unassigned: ৳${summary.unassigned}`, 14, 54);

  // ✅ Category table
  autoTable(doc, {
    startY: 65,
    head: [["Category", "Amount"]],
    body: categories.map(c => [
      c.categoryName,
      `৳${c.amount}`
    ]),
  });

  // ✅ Snapshot table
  autoTable(doc, {
    startY: doc.lastAutoTable.finalY + 10,
    head: [["Month", "Income", "Expense", "Assigned"]],
    body: snapshots.map(s => [
      `${s.month}/${s.year}`,
      s.income,
      s.expense,
      s.assigned,
    ]),
  });

  doc.save(`monthly-analytics-${summary.month}-${summary.year}.pdf`);
}
