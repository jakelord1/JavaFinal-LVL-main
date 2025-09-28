<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="WEB-INF/header.jsp"/>
        <div class="container py-4">
            <%
                String fromServlet = (String) request.getAttribute("HomeServlet");
                if (fromServlet == null) {
            %>
            <div class="alert alert-danger mb-4">Нелегальний запит в обхід сервлету</div>
            <%
                } else {
            %>
            <div class="row g-4">
                <div class="col-12 col-lg-8">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 mb-3">Java EE</h2>
                            <p class="mb-3">
                                Java Enterprise Edition - java + додаткові модулі для роботи з мережею.
                                Також до складу входить сервер застосунків (App Server),
                                проте, може знадобитись встановити його окремо:
                                <a class="link-primary" href="https://tomcat.apache.org/">Apache Tomcat</a>
                                <a class="link-primary ms-2" href="https://glassfish.org/">Eclipse GlassFish</a>
                                <a class="link-primary ms-2" href="https://www.wildfly.org/">WildFly</a>
                            </p>
                            <h2 class="h4 mt-4 mb-3">JSP</h2>
                            <p class="mb-3">
                                Java Server Pages - технології створення веб-сторінок
                            </p>
                            <h3 class="h5 mt-4 mb-3">Вирази</h3>
                            <p class="mb-3">
                                Інструкції задаються спецтегом
                                <code>&lt;% String str = "Hello World!";%&gt;</code>
                                <% String str = "Hello World!";%>
                                <br>
                                Вирази задаються спецтегом
                                <code>&lt;%= str + "!" %&gt; &rarr; <%= str + "!"%></code>
                            </p>
                            <h3 class="h5 mt-4 mb-3">Управління виконанням</h3>
                            <p class="mb-3">
                                <% String[] arr = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"}; %>
                                Спецоператорів JSP немає, управління формується засобами інструкцій.
                                <br>
                                Приклад з циклом:
                            </p>
                            <div class="bg-light border rounded p-3 mb-3">
                                <code>
                                    &lt;ul&gt;<br>
                                    &nbsp;&nbsp;&lt;% for (int i = 0; i &lt; arr.length; i++) { %&gt;<br>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&lt;li&gt;&lt;%= arr[i] %&gt;&lt;/li&gt;<br>
                                    &nbsp;&nbsp;&lt;% } %&gt;<br>
                                    &lt;/ul&gt;
                                </code>
                            </div>
                            <p class="mb-1">Результат виконання:</p>
                            <ul class="mt-2">
                                <% for (int i = 0; i < arr.length; i++) { %>
                                <li><%= arr[i]%></li>
                                <% } %>
                            </ul>
                            <p class="mt-3 mb-1">Вивести парні індекси курсивом, непарні — жирним:</p>
                            <ul class="mt-2">
                                <% for (int i = 0; i < arr.length; i++) { %>
                                <li>
                                    <% if (i % 2 == 0) { %>
                                    <i><%= arr[i]%></i>
                                    <% } else { %>
                                    <b><%= arr[i]%></b>
                                    <% } %>
                                </li>
                                <% } %>
                            </ul>
                            <h3 class="h5 mt-4 mb-3">Шаблонізація, підключення компонентів</h3>
                            <jsp:include page="WEB-INF/fragment.jsp">
                                <jsp:param name="key" value="Value"/>
                            </jsp:include>
                            <h3 class="h5 mt-4 mb-3">Передача даних від серверу</h3>
                            <div class="alert alert-info mb-0"><%= request.getAttribute("HomeServlet") %></div>
                        </div>
                    </div>
                </div>
                <div class="col-12 col-lg-4">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title mb-2">Сервіс мітки часу</h5>
                            <p class="card-text mb-3">Кількість секунд від UNIX-часу:</p>
                            <span class="display-6"><span class="badge bg-primary"><%= request.getAttribute("UnixTimestampSeconds") %></span></span>
                            <div class="mt-3 small text-muted">Оновлюється при кожному запиті сторінки</div>
                        </div>
                    </div>
                    <div class="card border-0 shadow-sm mt-4">
                        <div class="card-body">
                            <h5 class="card-title mb-2">Час у БД</h5>
                            <p class="card-text mb-3">Поточна дата-час з сервера БД:</p>
                            <span class="display-6"><span class="badge bg-secondary"><%= request.getAttribute("DbTime") %></span></span>
                        </div>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <jsp:include page="WEB-INF/footer.jsp"/>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>