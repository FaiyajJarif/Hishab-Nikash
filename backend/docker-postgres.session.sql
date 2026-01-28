SELECT
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
    'accounts','addresses','alerts',
    'billing_history','budget_items','budget_periods','categories',
    'category_goals','debt_goals',
    'email_verification_tokens','family_activity','family_budget_items',
    'family_budget_periods','family_categories',
    'family_expense','family_groups','family_invitations',
    'family_members','goal_contributions','goal_schedules','goals','monthly_analytics','notifications',
    'profiles','recurring_bill_idempotency','recurring_bill_run',
    'recurring_bill_runs','recurring_bills','subscriptions','targets',
    'transactions', 'user_household','user_onboarding_selections',
    'user_onboarding_source','user_profile','users'
  )
ORDER BY table_name, ordinal_position;
