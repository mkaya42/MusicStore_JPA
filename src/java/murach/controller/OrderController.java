/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package murach.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import murach.business.Cart;
import murach.business.Invoice;
import murach.business.LineItem;
import murach.business.Product;
import murach.business.User;
import murach.data.InvoiceDB;
import murach.data.ProductDB;
import murach.data.UserDB;
import murach.util.CookieUtil;
import murach.util.MailUtil;

/**
 *
 * @author 247940
 */
@WebServlet(name = "OrderController", urlPatterns = {"/order/*"})
public class OrderController extends HttpServlet {

    private static final String defaultURL = "/cart/cart.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet OrderController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet OrderController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = defaultURL;
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("/showChart")) {
            showChart(request, response);
        } else if (requestURI.endsWith("/checkUser")) {
            url = checkUser(request, response);
        }
        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private String showChart(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getCount() == 0) {
            request.setAttribute("emptyCart", "Your cart is empty..");
        } else {
            session.setAttribute("cart", cart);
        }

        return defaultURL;

    }

    private String checkUser(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String url = "";

        // if the User object exists with address1, skip User page
        if (user != null && !user.getAddress1().equals("")) {
            url = "/order/displayInvoice";
        } else {// otherwise, check the email cookie
            Cookie[] cookies = request.getCookies();
            String email = CookieUtil.getCookieValue(cookies, "emailCookie");
            System.out.println("mky1 -- "+email);
            if (email.equals("")) {
                user = new User();
                url = "/cart/user.jsp";
            } else {
                user = UserDB.selectUser(email);
                System.out.println("useraaa"+user.getFirstName());
                if (user != null && !user.getAddress1().equals("")) {
                    url = "/order/displayInvoice";
                }
            }
        }
        session.setAttribute("user", user);
        return url;
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = "";
        if (requestURI.endsWith("/addItem")) {
            url = addItem(request, response);
        } else if (requestURI.endsWith("/updateItem")) {
            url = updateItem(request, response);
        } else if (requestURI.endsWith("/removeItem")) {
            url = removeItem(request, response);
        } else if (requestURI.endsWith("/checkUser")) {
            url = checkUser(request, response);
        } else if (requestURI.endsWith("/processUser")) {
            url = processUser(request, response);
        } else if (requestURI.endsWith("/displayInvoice")) {
            url = displayInvoice(request, response);
        } else if (requestURI.endsWith("/displayUser")) {
            url = "/cart/user.jsp";
        } else if (requestURI.endsWith("/displayCreditCard")) {
            url = "/cart/credit_card.jsp";
        } else if (requestURI.endsWith("/completeOrder")) {
            url = completeOrder(request, response);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    private String addItem(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        String productCode = request.getParameter("productCode");
        Product product = ProductDB.selectProduct(productCode);
        if (product != null) {
            LineItem item = new LineItem();
            item.setProduct(product);
            cart.addItem(item);
        }

        session.setAttribute("cart", cart);
        return defaultURL;

    }

    private String updateItem(HttpServletRequest request, HttpServletResponse response) {
        String productCode = request.getParameter("productCode");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        Product product = ProductDB.selectProduct(productCode);
        if (product != null && cart != null) {
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            if (quantity < 0 || quantity == 0) {
                quantity = 1;
            }
            LineItem item = new LineItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            if (quantity > 0) {
                cart.addItem(item);
            } else {
                cart.removeItem(item);
            }
        }

        return defaultURL;
    }

    private String removeItem(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        String productCode = request.getParameter("productCode");
        Product product = ProductDB.selectProduct(productCode);

        if (product != null && cart != null) {
            LineItem item = new LineItem();
            item.setProduct(product);
            cart.removeItem(item);
        }
        return defaultURL;

    }

    private String processUser(HttpServletRequest request, HttpServletResponse response) {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String companyName = request.getParameter("companyName");
        String email = request.getParameter("email");
        String address1 = request.getParameter("address1");
        String address2 = request.getParameter("address2");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String zip = request.getParameter("zip");
        String country = request.getParameter("country");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            user = new User();
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCompanyName(companyName);
        user.setEmail(email);
        user.setAddress1(address1);
        user.setAddress2(address2);
        user.setCity(city);
        user.setState(state);
        user.setZip(zip);
        user.setCountry(country);

        if (UserDB.emailExists(email)) {
            UserDB.update(user);
        } else {
            UserDB.insert(user);
        }

        session.setAttribute("user", user);
        return "/order/displayInvoice";
    }

    private String displayInvoice(HttpServletRequest request, HttpServletResponse response) {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");
        java.util.Date today = new java.util.Date();
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(today);
        invoice.setUser(user);
        invoice.setLineItems(cart.getItems());
        session.setAttribute("invoice", invoice);
 
        return "/cart/invoice.jsp";
    }

    private String completeOrder(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Invoice invoice = (Invoice) session.getAttribute("invoice");
        User user = (User) session.getAttribute("user");

        String creditCardType = request.getParameter("creditCardType");
        String creditCardNumber = request.getParameter("creditCardNumber");
        String creditCardExpMonth = request.getParameter("creditCardExpirationMonth");
        String creditCardExpYear = request.getParameter("creditCardExpirationYear");

        user.setCreditCardType(creditCardType);
        user.setCreditCardNumber(creditCardNumber);
        user.setCreditCardExpirationDate(creditCardExpMonth+"/"+creditCardExpYear);
        
         // if a record for the User object exists, update it
         if(UserDB.emailExists(user.getEmail())){
             UserDB.update(user);
         }
         else {// otherwise, write a new record for the user
             UserDB.insert(user);
         }
         
         // write a new invoice record
         InvoiceDB.insert(invoice);
         // set the emailCookie in the user's browser.
         
         Cookie emailCookie = new Cookie("emailCookie", user.getEmail());
         emailCookie.setMaxAge(60*24*365*2*60);
         emailCookie.setPath("/");
         response.addCookie(emailCookie);

         
         // remove all items from the user's cart
         session.setAttribute("cart", null);
         
         // send an email to the user to confirm the order.
         String to =user.getEmail();
         String from="mkaya42@gmail.com";
         String subject="Order Confirmation";
         String body = "Dear " + user.getFirstName() + ",\n\n" +
            "Thanks for ordering from us. " +
            "You should receive your order in 3-5 business days. " + 
            "Please contact us if you have any questions.\n" +
            "Have a great day and thanks again!\n\n" +
            "Joe King\n" +
            "Fresh Corn Records";
          boolean isBodyHTML = false;
          
          try {
            MailUtil.sendMail(to, from, subject, body, isBodyHTML);
        }
        catch(MessagingException e) {
            this.log(
                "Unable to send email. \n" +
                "You may need to configure your system as " +
                "described in chapter 15. \n" +
                "Here is the email you tried to send: \n" +
                "=====================================\n" +
                "TO: " + to + "\n" +
                "FROM: " + from + "\n" +
                "SUBJECT: " + subject + "\n" +
                "\n" +
                body + "\n\n");
        }
        
        return "/cart/complete.jsp";
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
