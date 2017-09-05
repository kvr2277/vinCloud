var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/s3',{
            templateUrl: '/views/s3.html',
            controller: 's3Ctrl'
        })
        .when('/sqs',{
            templateUrl: '/views/sqs.html',
            controller: 'sqsCtrl'
        })
         .when('/sns',{
            templateUrl: '/views/sns.html',
            controller: 'snsCtrl'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

