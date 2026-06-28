import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


def load_clean_data(filename):
    df = pd.read_json(filename)
    return df


def generate_console_tables(df):
    # 1. Calculate average and max grades
    df['avg_grade'] = df['grades'].apply(lambda x: sum(x) / len(x) if isinstance(x, list) and len(x) > 0 else 0)
    df['max_grade'] = df['grades'].apply(lambda x: max(x) if isinstance(x, list) and len(x) > 0 else 0)

    # 2. Get the email domain
    df['domain'] = df['email'].apply(lambda email: str(email).split('@')[-1])

    # 3. Sort students into groups
    def categorize_student(avg):
        if avg < 3.0:
            return "At-Risk"
        elif avg <= 4.0:
            return "Average"
        else:
            return "Top Students"

    df['category'] = df['avg_grade'].apply(categorize_student)
    categories = ["At-Risk", "Average", "Top Students"]

    # 4. Print table for each group
    for cat in categories:
        print(f"\n=======================================================")
        print(f"                CATEGORY: {cat.upper()}")
        print(f"=======================================================")

        cat_df = df[df['category'] == cat]

        if cat_df.empty:
            print("No students in this category.")
            continue

        summary = cat_df.groupby('domain').agg(
            Total_Students=('name', 'count'),
            Avg_Grade=('avg_grade', 'mean'),
            Max_Grade=('max_grade', 'max')
        ).reset_index()

        summary['Avg_Grade'] = summary['Avg_Grade'].round(2)
        summary.rename(
            columns={'domain': 'Email Domain', 'Total_Students': 'Total Students', 'Avg_Grade': 'Average Grade',
                     'Max_Grade': 'Max Grade'}, inplace=True)

        print(summary.to_string(index=False))


def draw_charts(df):
    # 1. Calculate minimum grade, sum, and variance
    df['min_grade'] = df['grades'].apply(lambda x: np.min(x) if isinstance(x, list) and len(x) > 0 else 0)
    df['sum_grades'] = df['grades'].apply(lambda x: np.sum(x) if isinstance(x, list) and len(x) > 0 else 0)
    df['var_grade'] = df['grades'].apply(lambda x: np.var(x) if isinstance(x, list) and len(x) > 0 else 0)

    # 2. Create window for 3 charts
    fig, (ax1, ax2, ax3) = plt.subplots(1, 3, figsize=(16, 6))

    # 3. Draw Bar Chart for top 10 students
    top10 = df.nlargest(10, 'avg_grade')
    bar_colors = ['green' if lowest >= 3.5 else 'red' for lowest in top10['min_grade']]

    ax1.bar(top10['name'], top10['avg_grade'], color=bar_colors)
    ax1.set_title("TOP 10 Students (Avg Grade)")
    ax1.set_ylabel("Average Grade")
    ax1.set_xticks(range(len(top10)))
    ax1.set_xticklabels(top10['name'], rotation=45, ha='right')

    # 4. Draw Pie Chart for email domains
    domain_counts = df['domain'].value_counts()
    explode_settings = [0.1 if i == 0 else 0 for i in range(len(domain_counts))]

    ax2.pie(domain_counts, labels=domain_counts.index, autopct='%1.1f%%', explode=explode_settings)
    ax2.set_title("Email Domain Distribution")

    # 5. Draw Scatter Plot with different dot sizes
    dot_sizes = df['sum_grades'] * 5

    ax3.scatter(df['avg_grade'], df['var_grade'], s=dot_sizes, alpha=0.6, edgecolors='white')
    ax3.set_title("Grade Variance vs Average")
    ax3.set_xlabel("Average Grade")
    ax3.set_ylabel("Grade Variance")

    plt.tight_layout()
    plt.show()


if __name__ == '__main__':
    df_students = load_clean_data('s35774_report.json')

    print("Stage 3: Data loaded successfully.")
    print(f"Total students for analysis: {len(df_students)}")

    generate_console_tables(df_students)
    draw_charts(df_students)