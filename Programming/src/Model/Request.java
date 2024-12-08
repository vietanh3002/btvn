
package Model;

import VNPaySubsystem.Config;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Request {
    private Order order;

    public Request(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String buildQueryURL() {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan hang hoa";
        String orderType = "140000";
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = null;

        try {
            vnp_IpAddr = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException var22) {
            var22.printStackTrace();
        }

        String vnp_TmnCode = Config.vnp_TmnCode;
        String locate = "vn";
        Map vnp_Params = new HashMap();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((int)(this.order.getSubtotal() + this.order.getShipping()) * 100 * 1000));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", locate);
        vnp_Params.put("vnp_ReturnUrl", "ketquatrave");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(12, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();

        String fieldName;
        String fieldValue;
        while(itr.hasNext()) {
            fieldName = (String)itr.next();
            fieldValue = (String)vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName);
                hashData.append('=');

                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException var21) {
                    var21.printStackTrace();
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        fieldName = query.toString();
        fieldValue = Config.hmacSHA512(Config.secretKey, hashData.toString());
        fieldName = fieldName + "&vnp_SecureHash=" + fieldValue;
        String paymentUrl = Config.vnp_PayUrl + "?" + fieldName;
        System.out.print(paymentUrl);
        return paymentUrl;
    }
}

	
//package Model;
//
//import java.io.UnsupportedEncodingException;
//import java.net.InetAddress;
//import java.net.URLEncoder;
//import java.net.UnknownHostException;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.TimeZone;
//import javax.mail.*;
//import javax.mail.internet.*;
//
//import VNPaySubsystem.Config;
//
//public class Request {
//    private Order order;
//
//    public Request(Order order) {
//        this.order = order;
//    }
//
//    public Order getOrder() {
//        return order;
//    }
//
//    public void setOrder(Order order) {
//        this.order = order;
//    }
//
//    public String buildQueryURL() {
//        // Các thông số thanh toán VNPAY
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        String vnp_OrderInfo = "Thanh toan hang hoa";
//        String orderType = "140000";
//        String vnp_TxnRef = Config.getRandomNumber(8);
//        String vnp_IpAddr = null;
//        try {
//            vnp_IpAddr = InetAddress.getLocalHost().toString();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        String vnp_TmnCode = Config.vnp_TmnCode;
//        String locate = "vn";
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", vnp_Version);
//        vnp_Params.put("vnp_Command", vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_Amount", String.valueOf(((int)(order.getSubtotal() + order.getShipping())) * 100 * 1000)); // VND, nhân với 1000 vì số tiền tính theo đồng
//        vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_BankCode", "NCB");
//        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
//        vnp_Params.put("vnp_OrderType", orderType);
//        vnp_Params.put("vnp_Locale", locate);
//        vnp_Params.put("vnp_ReturnUrl", "ketquatrave");
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//
//        // Tạo thời gian cho vnp_CreateDate và vnp_ExpireDate
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//        cld.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//        // Xây dựng chuỗi query và mã bảo mật
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//        Iterator<String> itr = fieldNames.iterator();
//        while (itr.hasNext()) {
//            String fieldName = itr.next();
//            String fieldValue = vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//            	hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
//            	query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
//            	     .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
//
//                if (itr.hasNext()) {
//                    query.append('&');
//                    hashData.append('&');
//                }
//            }
//        }
//
//        String queryUrl = query.toString();
//        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
//        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
//
//        // Trả về paymentUrl trước
//        String finalPaymentUrl = paymentUrl;
//
//        // Gửi email thông báo cho khách hàng sau khi trả về URL thanh toán
//        new Thread(() -> sendEmail(order.getCustomerEmail(), "Thanh toán thành công", "Cảm ơn bạn đã thanh toán đơn hàng thành công!")).start();
//
//        return finalPaymentUrl;
//    }
//
//    private void sendEmail(String to, String subject, String body) {
//        final String username = "vietanhflt37@gmail.com"; // email của bạn
//        final String password = System.getenv("gepf rhqv ryfm cubt"); // Lấy mật khẩu từ biến môi trường
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(username));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//            message.setSubject(subject);
//            message.setText(body);
//
//            Transport.send(message);
//
//            System.out.println("Email sent successfully");
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
