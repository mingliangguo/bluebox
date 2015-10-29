<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="java.util.ResourceBundle" %>
<% ResourceBundle folderDetailResource = ResourceBundle.getBundle("folderDetails",request.getLocale());%>
<% ResourceBundle inboxResource = ResourceBundle.getBundle("inbox",request.getLocale());%>
<html ng-app="ionicApp">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Inbox</title>
   
    <link href="//code.ionicframework.com/nightly/css/ionic.css" rel="stylesheet">
    <script src="//code.ionicframework.com/nightly/js/ionic.bundle.js"></script>
    <script src="inboxes.js"></script>
  </head>

  <body ng-controller="InboxesCtrl">
    
    <ion-header-bar class="bar-positive">
      <h1 class="title"><%= folderDetailResource.getString("allMail") %></h1>
    </ion-header-bar>
 	<ion-header-bar class="bar-subheader item-input-inset">
      <label class="item-input-wrapper">
	    <i class="icon ion-ios7-search placeholder-icon"></i>
	    <input type="search" placeholder="<%= inboxResource.getString("searchPlaceholder") %>" ng-change="search(data.query)" ng-model="data.query">
	  </label>
    </ion-header-bar>

    <ion-content>   
      <ion-list>
      	<ion-item collection-repeat="item in data.items" item="item" href="inbox.jsp?Email={{item.label}}&State=1" class="item-remove-animate">{{ item.label }}</ion-item>
		<!-- <ion-infinite-scroll on-infinite="addItems('')"></ion-infinite-scroll> -->
      </ion-list>

    </ion-content>
      
  </body>
</html>