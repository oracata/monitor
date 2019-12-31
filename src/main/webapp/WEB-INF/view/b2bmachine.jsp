<%--
  Created by IntelliJ IDEA.
  User: oracat
  Date: 2019-12-31
  Time: 16:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<table>
<c:forEach items="${requestScope.b2bmachine}" var="b2bmachine" varStatus="stat">


        <td>${b2bmachine.cpu              }</td>



    </tr>
</c:forEach>
</table>
</body>
</html>
