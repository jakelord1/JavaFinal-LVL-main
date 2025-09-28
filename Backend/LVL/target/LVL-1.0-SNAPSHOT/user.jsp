<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="WEB-INF/header.jsp"/>
        <div class="container py-4">
            <%
                String userName = (String) request.getAttribute("userName");
                if (userName == null) {
            %>
            <div class="alert alert-danger">Нелегальний запит в обхід сервлету</div>
            <%
                } else {
            %>
            <h3 class="mb-3">Привіт, <span class="text-primary"><%= userName %></span>!</h3>
            <p class="mb-0">Доступ легальний. Дані передані від сервлета.</p>

            <div class="card mt-4">
                <div class="card-body">
                    <h5 class="card-title">Додаткова інформація</h5>
                    <p class="card-text">Поточний час сервера: 
                        <%= new java.util.Date() %>
                    </p>
                </div>
            </div>
            <%
                }
            %>
        </div>
        <jsp:include page="WEB-INF/footer.jsp"/>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
