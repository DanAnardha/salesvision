from flask import Flask, jsonify, request
import mysql.connector
from datetime import datetime, timedelta
import joblib
import pandas as pd

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

forecast_model = joblib.load('prophet_model.pkl')
recom_model = joblib.load('recom_model.pkl')

# Predict Forecast Model
def predict_sales(start_date, end_date):
    df = load_sales_data_from_db()
    days_to_predict = (end_date - df['ds'].max()).days
    future = forecast_model.make_future_dataframe(periods=days_to_predict)
    forecast = forecast_model.predict(future)
    forecast_filtered = forecast[(forecast['ds'] >= start_date) & (forecast['ds'] <= end_date)]
    return forecast_filtered[['ds', 'yhat']]

def load_sales_data_from_db():
    connection = mysql.connector.connect(**db_config)
    query = "SELECT Order_Date, Sales FROM orders"
    df = pd.read_sql(query, connection)
    connection.close()
    df['Order_Date'] = pd.to_datetime(df['Order_Date'])
    df = df.rename(columns={'Order_Date': 'ds', 'Sales': 'y'})
    return df

@app.route('/predict_forecast', methods=['GET'])
def predict():
    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')
    start_date = datetime.strptime(start_date_str, '%Y-%m-%d')
    end_date = datetime.strptime(end_date_str, '%Y-%m-%d')
    result = predict_sales(start_date, end_date)
    return jsonify(result.to_dict(orient='records'))


# Predict Price Recommendation Model
@app.route('/predict_recom', methods=['GET'])
def predict_recom():
    qty = request.args.get('qty', type=float)
    unit_price = request.args.get('unit_price', type=float)
    freight_price = request.args.get('freight_price', type=float)
    comp_1 = request.args.get('comp_1', type=float)
    product_score = request.args.get('product_score', type=float)

    if None in [qty, unit_price, freight_price, comp_1, product_score]:
        return jsonify({'error': 'Missing required parameters'}), 400
    input_data = pd.DataFrame([{
        'qty': qty,
        'unit_price': unit_price,
        'freight_price': freight_price,
        'comp_1': comp_1,
        'product_score': product_score
    }])
    required_columns = ['qty', 'unit_price', 'freight_price', 'comp_1', 'product_score']
    prediction = recom_model.predict(input_data[required_columns])
    return jsonify({'prediction': prediction.tolist()})

@app.route('/api/sales_by_state', methods=['GET'])
def get_order_sales_by_state():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        query = """
        SELECT 
            StateProvince,
            SUM(Sales) AS Total_Sales,
            SUM(Profit) AS Total_Profit,
            SUM(Quantity) AS Total_Quantity
        FROM orders
        GROUP BY StateProvince
        ORDER BY StateProvince ASC
        """
        cursor.execute(query)
        order_sales_by_state = cursor.fetchall()
        return jsonify(order_sales_by_state)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/order_by_shipmode', methods=['GET'])
def get_orders_by_ship_mode():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        query = """
        SELECT 
            Ship_Mode,
            COUNT(Order_ID) AS Total_Order
        FROM orders
        GROUP BY Ship_Mode
        ORDER BY Ship_Mode ASC
        """
        cursor.execute(query)
        orders_by_ship_mode = cursor.fetchall()
        return jsonify(orders_by_ship_mode)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/profit_by_manager', methods=['GET'])
def get_profit_by_manager():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        
        query = """
        SELECT 
            m.Manager_Name,
            SUM(o.Sales) AS Total_Sales,
            SUM(o.Profit) AS Total_Profit,
            COUNT(o.Order_ID) AS Total_Orders
        FROM orders o
        JOIN manager m ON o.Region = m.region
        GROUP BY m.Manager_Name
        ORDER BY m.Manager_Name ASC
        """
        cursor.execute(query)
        profit_by_manager = cursor.fetchall()
        return jsonify(profit_by_manager)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/profit_by_manager_year', methods=['GET'])
def get_profit_by_manager_year():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        query = """
        SELECT 
            m.Manager_Name,
            YEAR(o.Order_Date) AS Year,
            SUM(o.Sales) AS Total_Sales,
            SUM(o.Profit) AS Total_Profit,
            COUNT(o.Order_ID) AS Total_Orders,
            o.Category
        FROM orders o
        JOIN manager m ON o.Region = m.Region
        GROUP BY m.Manager_Name, YEAR(o.Order_Date), o.Category
        ORDER BY m.Manager_Name, Year ASC;
        """
        cursor.execute(query)
        profit_by_manager_year = cursor.fetchall()
        return jsonify(profit_by_manager_year)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/order_sales', methods=['GET'])
def get_order_sales():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        query = """
        SELECT 
            Order_Date,
            SUM(Sales) AS Total_Sales
        FROM orders
        GROUP BY Order_Date
        ORDER BY Order_Date ASC
        """
        cursor.execute(query)
        order_sales = cursor.fetchall()
        return jsonify(order_sales)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/profit_by_category_per_month', methods=['GET'])
def get_profit_by_category_per_month():
    try:
        start_date = request.args.get('start_date', '2021-01-01')
        end_date = request.args.get('end_date', '2030-12-31')
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT 
            DATE_FORMAT(Order_Date, '%Y-%m') AS Month,
            Category,
            SUM(Profit) AS Total_Profit
        FROM orders
        WHERE Order_Date BETWEEN %s AND %s
        GROUP BY Month, Category
        ORDER BY Month ASC, Category ASC
        """
        cursor.execute(query, (start_date, end_date))
        profit_by_category_per_month = cursor.fetchall()

        return jsonify(profit_by_category_per_month)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


@app.route('/api/profit_by_region_per_month', methods=['GET'])
def get_profit_by_region_per_month():
    try:
        start_date = request.args.get('start_date', '2021-01-01')
        end_date = request.args.get('end_date', '2030-12-31')
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT 
            DATE_FORMAT(Order_Date, '%Y-%m') AS Month,
            Region,
            SUM(Profit) AS Total_Profit
        FROM orders
        WHERE Order_Date BETWEEN %s AND %s
        GROUP BY Month, Region
        ORDER BY Month ASC, Region ASC
        """
        cursor.execute(query, (start_date, end_date))
        profit_by_region_per_month = cursor.fetchall()

        return jsonify(profit_by_region_per_month)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


@app.route('/api/profit_by_segment_per_month', methods=['GET'])
def get_profit_by_segment_per_month():
    try:
        start_date = request.args.get('start_date', '2021-01-01')
        end_date = request.args.get('end_date', '2030-12-31')
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT 
            DATE_FORMAT(Order_Date, '%Y-%m') AS Month,
            Segment,
            SUM(Profit) AS Total_Profit
        FROM orders
        WHERE Order_Date BETWEEN %s AND %s
        GROUP BY Month, Segment
        ORDER BY Month ASC, Segment ASC
        """
        cursor.execute(query, (start_date, end_date))
        profit_by_segment_per_month = cursor.fetchall()

        return jsonify(profit_by_segment_per_month)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/sales_by_month', methods=['GET'])
def get_sales_by_month():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        cursor.execute("""
            SELECT 
                DATE_FORMAT(Order_Date, '%Y-%m') AS Month,
                SUM(Sales) AS Total_Sales,
                SUM(Profit) AS Total_Profit,
                SUM(Quantity) AS Total_Quantity,
                COUNT(Order_ID) AS Total_Order,
                COUNT(DISTINCT Customer_ID) AS Total_Customer
            FROM orders
            GROUP BY Month
            ORDER BY Month ASC
        """)
        sales_by_month = cursor.fetchall()
        return jsonify(sales_by_month)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


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

@app.route('/api/sales_by_segment', methods=['GET'])
def get_sales_by_segment():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        # Query to calculate sales by segment
        cursor.execute("""
            SELECT Segment, SUM(Sales) AS Total_Sales,
            SUM(Quantity) AS Total_Quantity
            FROM orders 
            GROUP BY Segment 
            ORDER BY Total_Sales DESC
        """)
        sales_by_segment = cursor.fetchall()
        return jsonify(sales_by_segment)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/sales_by_subcategory', methods=['GET'])
def get_sales_by_subcategory():
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)
        # Query to calculate sales by segment
        cursor.execute("""
            SELECT SubCategory, 
            SUM(Sales) AS Total_Sales 
            FROM orders 
            GROUP BY SubCategory 
            ORDER BY Total_Sales DESC LIMIT 10
        """)
        sales_by_segment = cursor.fetchall()
        return jsonify(sales_by_segment)
    except mysql.connector.Error as err:
        return jsonify({'error': str(err)}), 500
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

@app.route('/api/get_order_dates', methods=['GET'])
def get_order_dates():
    connection = mysql.connector.connect(**db_config)
    cursor = connection.cursor(dictionary=True)
    query = "SELECT MIN(Order_Date) AS earliest_order, MAX(Order_Date) AS latest_order FROM orders"
    cursor.execute(query)
    order_dates = cursor.fetchall()
    cursor.close()
    connection.close()
    return jsonify(order_dates)

if __name__ == '__main__':
    app.run(debug=True)
