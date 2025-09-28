<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="card border-0 shadow-sm">
    <div class="card-body">
        <h5 class="card-title mb-2">Фрагмент сторінки</h5>
        <p class="card-text mb-1">Ключ фрагмента:</p>
        <span class="badge bg-secondary"><%= request.getParameter("key") %></span>
    </div>
</div>
