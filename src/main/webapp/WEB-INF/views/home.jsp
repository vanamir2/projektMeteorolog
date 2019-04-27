<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>PPJ Projekt Vana</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/printCountries">Print all countries</a></p>
<p><a href="${pageContext.request.contextPath}/printCities">Print all cities</a></p>
<p><a href="${pageContext.request.contextPath}/printMeasurements">Print all measurements</a></p>
<p><a href="${pageContext.request.contextPath}/measurementByCountry">Measurement by country</a></p>

</body>
</html>