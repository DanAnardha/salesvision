from flask import Flask, jsonify
import mysql.connector
from datetime import datetime, timedelta

app = Flask(__name__)

db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'superstore'
}

# @app.route('/api/users', methods=['GET'])
# def get_users():
#     try:
#         connection = mysql.connector.connect(**db_config)
#         cursor = connection.cursor(dictionary=True)
#         cursor.execute("SELECT * from orders")
#         # cursor.execute("SELECT id_pinjam, id_petugas, total_pinjam FROM peminjaman ORDER BY tanggal_pinjam DESC LIMIT 50")
#         users = cursor.fetchall()
#         return jsonify(users)
#     except mysql.connector.Error as err:
#         return jsonify({'error': str(err)}), 500
#     finally:
#         if connection.is_connected():
#             cursor.close()
#             connection.close()

@app.route('/api/sales_by_region', methods=['GET'])
def get_sales_by_region():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT Region, SUM(Sales) AS Total_Sales, SUM(Profit) AS Total_Profit from orders group by Region order by Total_Sales desc")
        sales_by_region = cursor.fetchall()
        return jsonify(sales_by_region)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/sales_by_category', methods=['GET'])
def get_sales_by_category():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        # cursor.execute("SELECT Category, SUM(Sales) AS Total_Sales, SUM(Profit) AS Total_Profit FROM orders GROUP BY Category ORDER BY Total_Sales DESC")
        cursor.execute("SELECT Category, SUM(Sales) AS Total_Sales FROM orders GROUP BY Category ORDER BY Total_Sales DESC")
        sales_by_region = cursor.fetchall()
        return jsonify(sales_by_region)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == '__main__':
    app.run(debug=True)
