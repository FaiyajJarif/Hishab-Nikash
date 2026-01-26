SELECT
    AVG(t.amount) AS avg_spending
FROM transactions t
JOIN users u ON u.id = t.user_id
WHERE t.type = 'EXPENSE'
  AND EXTRACT(YEAR FROM AGE(u.date_of_birth)) BETWEEN 26 AND 35
  AND t.date BETWEEN DATE '2024-01-01' AND DATE '2024-01-31';
