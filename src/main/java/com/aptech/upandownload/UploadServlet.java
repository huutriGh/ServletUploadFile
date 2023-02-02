package com.aptech.upandownload;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Khai bao Servlet nhan
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // Khi kich thuoc file du 1mb se duoc ghi xuong o dia. thay vi tren bo nho dem
        maxFileSize = 1024 * 1024 * 5, // Dung luong toi da cua file upload (5 mb)
        maxRequestSize = 1024 * 1024 * 5 * 5) // Tong dung luong toi da cua file trong moi request (50 mb)

@WebServlet(
        urlPatterns = "/upload",
        // Khai bao ten thu muc mac dinh luu file duoc upload len
        initParams = {@WebInitParam(name = "destinationPath", value = "upload")})
public class UploadServlet extends HttpServlet {

    private String destinationPath;

    @Override
    public void init() throws ServletException {
        // Duong dan file duoc upload len. ( Khi chạy tomcat va upload file se duocj luu vao thu muc target của project voi ten thu muc duoc khai bao
        destinationPath = getServletContext().getRealPath("") + File.separator + getInitParameter("destinationPath");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Tao array luu ten cac file da duoc upload len
        List<String> listFile = new ArrayList<>();
        // Tao duong dan chua file tren o cung
        File fileUploadDir = new File(destinationPath);
        // Kiem tra neu thu muc chua ton tai thì se tao moi.
        if (!fileUploadDir.exists()) {
            fileUploadDir.mkdirs();
        }
        // Lay cac file duoc upload len
        for (Part part : req.getParts()) {
            // Lay ten file duoc upload len
            String fileName = getFileName(part);
            // Save file xuong o dia
            String fullPath = fileUploadDir + File.separator + fileName;
            part.write(fullPath);
            listFile.add(fullPath);

        }
        // tra ve thong tin danh sach file da upload
        resp.getWriter().write("Upload Successfully:\n" + String.join("\n", listFile));
    }

    /**
     * Ham doc ten file tu multi-part.
     *
     * @param part
     * @return Ten file. Neu khong doc duoc ten file thì ten file mac dinh duoc tu tao ngau nhien
     */
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename"))
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
        try {
            return File.createTempFile("", "").getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
