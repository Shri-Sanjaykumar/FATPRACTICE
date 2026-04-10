import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting EAZEGoo Grocery App on port 8080...");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Setup API and UI Endpoints
        server.createContext("/", new UIHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/checkout", new CheckoutHandler());
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started. Access at http://localhost:8080/");
    }

    // ==========================================
    // Core Business Logic (Tested by JUnit)
    // ==========================================
    
    public static boolean validateLogin(String username, String password) {
        // Mock DB Check: admin/admin123
        return "admin".equals(username) && "admin123".equals(password);
    }

    public static boolean validateOrder(int itemCount, double totalAmount) {
        return itemCount > 0 && totalAmount > 0.0;
    }

    public static boolean validateDeliverySlot(int hourOfDay) {
        // Valid delivery slots are between 8 AM (8) and 8 PM (20)
        return hourOfDay >= 8 && hourOfDay <= 20;
    }

    // ==========================================
    // HTTP Handlers (REST Backend)
    // ==========================================
    
    static class UIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] responseBytes = getHtml().getBytes(StandardCharsets.UTF_8);
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                InputStream is = t.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String[] pairs = body.split("&");
                String user = "";
                String pass = "";
                for(String pair : pairs) {
                    String[] kv = pair.split("=");
                    if(kv.length == 2) {
                        if(kv[0].equals("username")) user = kv[1];
                        if(kv[0].equals("password")) pass = kv[1];
                    }
                }
                
                boolean isValid = validateLogin(user, pass);
                String response = isValid ? "Approved" : "Invalid credentials! Use admin / admin123";
                int code = isValid ? 200 : 401;
                
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                t.sendResponseHeaders(code, responseBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }
    }

    static class CheckoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                InputStream is = t.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                String[] pairs = body.split("&");
                int items = 0;
                double total = 0.0;
                int hour = 0;
                
                for(String pair : pairs) {
                    String[] kv = pair.split("=");
                    if(kv.length == 2) {
                        if(kv[0].equals("items")) items = Integer.parseInt(kv[1]);
                        if(kv[0].equals("total")) total = Double.parseDouble(kv[1]);
                        if(kv[0].equals("hour")) hour = Integer.parseInt(kv[1]);
                    }
                }
                
                boolean orderValid = validateOrder(items, total);
                boolean slotValid = validateDeliverySlot(hour);
                
                String response;
                int code;
                if(orderValid && slotValid) {
                    response = "Success: Order confirmed and your grocery delivery slot is booked!";
                    code = 200;
                } else if (!orderValid) {
                    response = "Error: Invalid order. You must add at least 1 item to the cart.";
                    code = 400;
                } else {
                    response = "Error: Invalid delivery slot. Must be between 8:00 AM and 8:00 PM.";
                    code = 400;
                }
                
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                t.sendResponseHeaders(code, responseBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }
    }

    // ==========================================
    // Ultimate Full-Stack Single Page App String
    // ==========================================
    
    public static String getHtml() {
        return "<!DOCTYPE html>\n" +
"<html lang='en'>\n" +
"<head>\n" +
"    <meta charset='UTF-8'>\n" +
"    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
"    <title>EAZEGoo Grocery</title>\n" +
"    <link href='https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap' rel='stylesheet'>\n" +
"    <style>\n" +
"        * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Outfit', sans-serif; }\n" +
"        body { background-color: #f4f6f8; color: #333; overflow-x: hidden; }\n" +
"        \n" +
"        /* LOGIN SCREEN STYLES */\n" +
"        #login-screen { display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #1b5e20, #4caf50); }\n" +
"        .login-box { background: white; padding: 40px; border-radius: 20px; box-shadow: 0 15px 30px rgba(0,0,0,0.2); width: 100%; max-width: 400px; text-align: center; }\n" +
"        .login-box h2 { font-size: 28px; font-weight: 700; color: #1b5e20; margin-bottom: 30px; display: flex; justify-content: center; align-items: center; gap: 10px; }\n" +
"        .login-box input { width: 100%; padding: 15px; margin-bottom: 20px; border: 2px solid #eee; border-radius: 10px; font-size: 16px; outline: none; transition: border-color 0.3s; }\n" +
"        .login-box input:focus { border-color: #4caf50; }\n" +
"        .login-btn { background: #4caf50; color: white; border: none; padding: 15px; width: 100%; border-radius: 10px; font-size: 18px; font-weight: 600; cursor: pointer; transition: all 0.3s; }\n" +
"        .login-btn:hover { background: #388e3c; transform: translateY(-3px); box-shadow: 0 10px 20px rgba(76,175,80,0.3); }\n" +
"        #login-error { color: #e74c3c; font-weight: 600; margin-top: 15px; display: none; font-size: 14px; }\n" +
"        \n" +
"        /* STOREFRONT STYLES */\n" +
"        header { background: #1b5e20; color: white; padding: 20px 50px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); position: sticky; top: 0; z-index: 100; }\n" +
"        header h1 { font-size: 26px; font-weight: 700; display: flex; align-items: center; gap: 8px; }\n" +
"        .cart-icon { background: #fff; color: #1b5e20; padding: 10px 20px; border-radius: 30px; font-weight: 700; cursor: pointer; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
"        .hero { background: linear-gradient(rgba(27, 94, 32, 0.7), rgba(0, 0, 0, 0.6)), url('https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80') center/cover; color: white; text-align: center; padding: 100px 20px; }\n" +
"        .hero h2 { font-size: 48px; margin-bottom: 15px; font-weight: 700; }\n" +
"        .hero p { font-size: 20px; font-weight: 300; }\n" +
"        .container { max-width: 1300px; margin: 40px auto; padding: 0 20px; display: grid; grid-template-columns: 3fr 1fr; gap: 40px; }\n" +
"        .products { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 25px; }\n" +
"        .product-card { background: white; border-radius: 12px; padding: 20px; text-align: center; box-shadow: 0 4px 10px rgba(0,0,0,0.04); transition: all 0.3s ease; border: 1px solid #eaeaea; position: relative; overflow: hidden; }\n" +
"        .product-card:hover { transform: translateY(-8px); box-shadow: 0 12px 20px rgba(0,0,0,0.1); border-color: #4caf50; }\n" +
"        .product-card img { width: 140px; height: 140px; object-fit: cover; border-radius: 50%; margin-bottom: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); transition: transform 0.3s; }\n" +
"        .product-card:hover img { transform: scale(1.05); }\n" +
"        .product-card h3 { font-size: 18px; margin-bottom: 8px; color: #2c3e50; font-weight: 600; }\n" +
"        .product-card .price { color: #2e7d32; font-weight: 700; font-size: 20px; margin-bottom: 20px; }\n" +
"        .btn { background: #4caf50; color: white; border: none; padding: 12px 15px; width: 100%; border-radius: 8px; cursor: pointer; font-size: 15px; font-weight: 600; transition: all 0.2s; }\n" +
"        .btn:hover { background: #388e3c; transform: scale(1.02); }\n" +
"        .sidebar { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); height: fit-content; position: sticky; top: 100px; border: 1px solid #eaeaea; }\n" +
"        .sidebar h2 { margin-bottom: 25px; border-bottom: 2px solid #f0f0f0; padding-bottom: 15px; color: #2c3e50; }\n" +
"        .cart-item { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 15px; border-bottom: 1px dashed #f0f0f0; padding-bottom: 10px; }\n" +
"        .total { display: flex; justify-content: space-between; font-weight: 700; font-size: 22px; margin-top: 25px; color: #1b5e20; }\n" +
"        .checkout-form { margin-top: 30px; }\n" +
"        .checkout-form label { display: block; margin-bottom: 10px; font-size: 14px; font-weight: 600; color: #555; }\n" +
"        .checkout-form select { width: 100%; padding: 12px; border: 2px solid #e0e0e0; border-radius: 8px; margin-bottom: 25px; outline: none; font-size: 15px; transition: border-color 0.2s; }\n" +
"        .checkout-form select:focus { border-color: #4caf50; }\n" +
"    </style>\n" +
"</head>\n" +
"<body>\n" +
"\n" +
"<!-- LOGIN SCREEN -->\n" +
"<div id='login-screen'>\n" +
"    <div class='login-box'>\n" +
"        <h2>🛒 EAZEGoo Login</h2>\n" +
"        <p style='color:#7f8c8d; margin-bottom: 25px; font-size: 14px;'>Use <strong>admin</strong> and <strong>admin123</strong> to proceed.</p>\n" +
"        <input type='text' id='username' placeholder='Username' autocomplete='off' />\n" +
"        <input type='password' id='password' placeholder='Password' autocomplete='off' />\n" +
"        <button class='login-btn' onclick='attemptLogin()'>Secure Login</button>\n" +
"        <div id='login-error'></div>\n" +
"    </div>\n" +
"</div>\n" +
"\n" +
"<!-- MAIN STOREFRONT SCREEN (Hidden by Default) -->\n" +
"<div id='store-screen' style='display: none;'>\n" +
"    <header>\n" +
"        <h1>🛒 EAZEGoo Grocery</h1>\n" +
"        <div class='cart-icon'>Total: $<span id='nav-total'>0.00</span></div>\n" +
"    </header>\n" +
"\n" +
"    <div class='hero'>\n" +
"        <h2>Fresh Organic Groceries, Delivered.</h2>\n" +
"        <p>Premium quality direct from the farm, straight to your doorstep.</p>\n" +
"    </div>\n" +
"\n" +
"    <div class='container'>\n" +
"        <div class='products'>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1560806887-1e4cd0b6fac6?w=300' alt='Apple'>\n" +
"                <h3>Fresh Fuji Apples</h3>\n" +
"                <div class='price'>$2.99</div>\n" +
"                <button class='btn' onclick='addToCart(\"Fresh Fuji Apples\", 2.99)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1603833665858-e61d17a86224?w=300' alt='Banana'>\n" +
"                <h3>Organic Bananas</h3>\n" +
"                <div class='price'>$1.49</div>\n" +
"                <button class='btn' onclick='addToCart(\"Organic Bananas\", 1.49)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1550583724-b2692b85b150?w=300' alt='Milk'>\n" +
"                <h3>Whole Milk (1 Gal)</h3>\n" +
"                <div class='price'>$3.49</div>\n" +
"                <button class='btn' onclick='addToCart(\"Whole Milk\", 3.49)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1509440159596-0249088772ff?w=300' alt='Bread'>\n" +
"                <h3>Artisan Bread</h3>\n" +
"                <div class='price'>$2.49</div>\n" +
"                <button class='btn' onclick='addToCart(\"Artisan Bread\", 2.49)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1498654077810-12c21d4d6dc3?w=300' alt='Eggs'>\n" +
"                <h3>Farm Eggs (12 pk)</h3>\n" +
"                <div class='price'>$4.99</div>\n" +
"                <button class='btn' onclick='addToCart(\"Farm Eggs (12 pk)\", 4.99)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"            <div class='product-card'>\n" +
"                <img src='https://images.unsplash.com/photo-1613478223719-2ab802602423?w=300' alt='Orange'>\n" +
"                <h3>Pure Orange Juice</h3>\n" +
"                <div class='price'>$5.99</div>\n" +
"                <button class='btn' onclick='addToCart(\"Orange Juice\", 5.99)'>+ Add to Cart</button>\n" +
"            </div>\n" +
"        </div>\n" +
"\n" +
"        <div class='sidebar'>\n" +
"            <h2>📝 Your Order</h2>\n" +
"            <div id='cart-items'>\n" +
"                <div style='color: #888; font-size: 14px; font-style: italic;'>Your cart is currently empty.</div>\n" +
"            </div>\n" +
"            <div class='total'>\n" +
"                <span>Total:</span>\n" +
"                <span>$<span id='cart-total'>0.00</span></span>\n" +
"            </div>\n" +
"            \n" +
"            <div class='checkout-form'>\n" +
"                <label>Select Delivery Slot:</label>\n" +
"                <select id='delivery-slot'>\n" +
"                    <option value='06'>06:00 AM (Invalid Slot)</option>\n" +
"                    <option value='08' selected>08:00 AM - 10:00 AM</option>\n" +
"                    <option value='12'>12:00 PM - 02:00 PM</option>\n" +
"                    <option value='18'>06:00 PM - 08:00 PM</option>\n" +
"                    <option value='22'>10:00 PM (Invalid Slot)</option>\n" +
"                </select>\n" +
"                <button class='btn' style='font-size: 18px; padding: 15px; background: #e67e22;' onclick='checkout()'>Proceed to Checkout ➡️</button>\n" +
"            </div>\n" +
"        </div>\n" +
"    </div>\n" +
"</div>\n" +
"\n" +
"<script>\n" +
"    // --- LOGIN LOGIC ---\n" +
"    async function attemptLogin() {\n" +
"        const user = document.getElementById('username').value;\n" +
"        const pass = document.getElementById('password').value;\n" +
"        const errBox = document.getElementById('login-error');\n" +
"        errBox.style.display = 'none';\n" +
"        \n" +
"        try {\n" +
"            const res = await fetch('/api/login', {\n" +
"                method: 'POST',\n" +
"                body: `username=${encodeURIComponent(user)}&password=${encodeURIComponent(pass)}`\n" +
"            });\n" +
"            const text = await res.text();\n" +
"            if (res.ok) {\n" +
"                // Success! Transition UI\n" +
"                document.getElementById('login-screen').style.display = 'none';\n" +
"                document.getElementById('store-screen').style.display = 'block';\n" +
"            } else {\n" +
"                errBox.innerText = '❌ ' + text;\n" +
"                errBox.style.display = 'block';\n" +
"            }\n" +
"        } catch(e) {\n" +
"            errBox.innerText = '❌ Backend connection error!';\n" +
"            errBox.style.display = 'block';\n" +
"        }\n" +
"    }\n" +
"\n" +
"    // --- STORE LOGIC ---\n" +
"    let cart = [];\n" +
"    let total = 0;\n" +
"    \n" +
"    function addToCart(name, price) {\n" +
"        cart.push({name, price});\n" +
"        total += price;\n" +
"        updateCartUI();\n" +
"    }\n" +
"    \n" +
"    function updateCartUI() {\n" +
"        const cartContainer = document.getElementById('cart-items');\n" +
"        if(cart.length === 0) {\n" +
"            cartContainer.innerHTML = \"<div style='color: #888; font-size: 14px; font-style: italic;'>Your cart is currently empty.</div>\";\n" +
"        } else {\n" +
"            cartContainer.innerHTML = '';\n" +
"            cart.forEach(item => {\n" +
"                cartContainer.innerHTML += `<div class='cart-item'><span>${item.name}</span><strong>$${item.price.toFixed(2)}</strong></div>`;\n" +
"            });\n" +
"        }\n" +
"        document.getElementById('cart-total').innerText = total.toFixed(2);\n" +
"        document.getElementById('nav-total').innerText = total.toFixed(2);\n" +
"    }\n" +
"    \n" +
"    async function checkout() {\n" +
"        const hour = document.getElementById('delivery-slot').value;\n" +
"        const body = `items=${cart.length}&total=${total.toFixed(2)}&hour=${hour}`;\n" +
"        \n" +
"        try {\n" +
"            const res = await fetch('/api/checkout', {\n" +
"                method: 'POST',\n" +
"                body: body\n" +
"            });\n" +
"            const text = await res.text();\n" +
"            if(res.ok) {\n" +
"                alert('🎉 ' + text);\n" +
"                cart = [];\n" +
"                total = 0;\n" +
"                updateCartUI();\n" +
"            } else {\n" +
"                alert('⚠️ ' + text);\n" +
"            }\n" +
"        } catch(e) {\n" +
"            alert('Error connecting to backend server.');\n" +
"        }\n" +
"    }\n" +
"</script>\n" +
"</body>\n" +
"</html>";
    }
}
