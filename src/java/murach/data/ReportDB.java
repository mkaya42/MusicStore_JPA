/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import murach.business.Download;
import murach.business.User;
import murach.util.DBUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author 247940
 */
public class ReportDB {

    public static Workbook getUserEmail() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String query = "Select u from User u order by u.lastName";
        TypedQuery<User> q = em.createQuery(query, User.class);
        List<User> users = null;
        try {
            users = q.getResultList();
        } catch (NoResultException e) {
            System.out.println(e);
        } finally {
            em.close();
        }
        // create the workbook, its worksheet, and its title row

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Email Report");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("The User Email Report");

        // create the header row
        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Last Name");
        row.createCell(1).setCellValue("FirstName");
        row.createCell(2).setCellValue("Email");
        row.createCell(3).setCellValue("CompanyName");
        row.createCell(4).setCellValue("Address1");
        row.createCell(5).setCellValue("Address2");
        row.createCell(6).setCellValue("City");
        row.createCell(7).setCellValue("State");
        row.createCell(8).setCellValue("Zip");
        row.createCell(9).setCellValue("Country");
        row.createCell(10).setCellValue("UserID");

        // create the data rows
        int i = 3;
        for (User user : users) {
            row = sheet.createRow(i);
            row.createCell(0).setCellValue(user.getLastName());
            row.createCell(1).setCellValue(user.getFirstName());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getCompanyName());
            row.createCell(4).setCellValue(user.getAddress1());
            row.createCell(5).setCellValue(user.getAddress2());
            row.createCell(6).setCellValue(user.getCity());
            row.createCell(7).setCellValue(user.getState());
            row.createCell(8).setCellValue(user.getZip());
            row.createCell(9).setCellValue(user.getCountry());
            row.createCell(10).setCellValue(user.getId());
            i++;
        }
        return workbook;
    }

    public static Workbook getDownloadDetail(String startDate, String endDate) {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String qString = "SELECT d from Download d "
                + "WHERE d.downloadDate >= :startDate AND "
                + "d.downloadDate <= :endDate ORDER BY d.downloadDate DESC";

        String query = "Select d from Download d where d.downloadDate>= :startDate And d.downloadDate<= :endDate"
                + "order by d.downloadDate DESC";

        TypedQuery<Download> q = em.createQuery(query, Download.class);
        List<Download> downloads = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            q.setParameter("startDate", dateFormat.format(startDate));
            q.setParameter("endDate", dateFormat.format(endDate));
            downloads = q.getResultList();

        } catch (Exception e) {
            System.err.println(e);
            return null;
        } finally {
            em.close();
        }
        // create the workbook, its worksheet, and its title row
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Download Report");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("The Download Report");

        // create the header rows
        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Start Date " + startDate);
        row = sheet.createRow(3);
        row.createCell(0).setCellValue("End Date " + endDate);

        row = sheet.createRow(5);
        row.createCell(0).setCellValue("Download Date");
        row.createCell(1).setCellValue("Product Code");
        row.createCell(2).setCellValue("Email");
        row.createCell(3).setCellValue("FirstName");
        row.createCell(4).setCellValue("LastName");

        int i = 6;
        int total = 0;
        for (Download download : downloads) {
            row = sheet.createRow(i);
            row.createCell(0).setCellValue(download.getDownloadDate());
            row.createCell(1).setCellValue(download.getProductCode());
            row.createCell(2).setCellValue(download.getUser().getEmail());
            row.createCell(3).setCellValue(download.getUser().getFirstName());
            row.createCell(4).setCellValue(download.getUser().getLastName());
            i++;
            total++;
        }
        row = sheet.createRow(i + 1);
        row.createCell(0).setCellValue("Total Number of Downloads " + total);

        return workbook;
    }
}
