/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var mainapp = angular.module("mainApp", ["ngRoute"]);
mainapp.value("pageData",{
    pageFormat: "Frontpage"    
});


mainapp.config(['$routeProvider',function($routeProvider){
   $routeProvider.
    when("/Boardgames", {
        templateUrl : "Layouts/GamesList.html", 
        controller: "gameslistCtrl" 
    }).
    when("/Spillinfo/:gameID", {
        templateUrl : "Layouts/GameInfo.html", 
        controller: "gameinfoCtrl" 
    }). 
    when("/Login",{
        templateUrl : "Layouts/Login.html", 
        controller: "loginCtrl"         
    }).
    when("/Logout",{
        templateUrl : "Layouts/Login.html", 
        controller: "logoutCtrl"         
    }).            
    when("/redigerSpill/:gameID", {
        templateUrl: "Layouts/EditGame.html",
        controller: "editgameCtrl"
    }).
    when("/redigerFraksjon/:frakID/:spillID", {
        templateUrl: "Layouts/EditFaction.html",
        controller: "editfactionCtrl"
    }).  
    when("/redigerScenario/:scenID/:spillID", {
        templateUrl: "Layouts/EditScenario.html",
        controller: "editscenarioCtrl"
    }).            
    when("/redigerSesjon/:sesjonID/:spillID", {
        templateUrl: "Layouts/LogGame.html",
        controller: "gameloggerCtrl"
    }). 
    when("/redigerSesjon/:sesjonID/:spillID/:presetsID", {
        templateUrl: "Layouts/LogGame.html",
        controller: "gameloggerCtrl"
    }).             
    when("/sesjonsliste/", {
        templateUrl: "Layouts/SessionList.html",
        controller: "sessionlistCtrl"
    }).
    when("/sesjoninfo/:sesjonID", {
        templateUrl: "Layouts/SpillSesjon.html",
        controller: "sessionVisCtrl"
    }).
    when("/spillerliste/", {
        templateUrl: "Layouts/PlayerList.html",
        controller: "spillerlisteCtrl"
    }).
    when("/spillerinfo/:spillerID", {
        templateUrl: "Layouts/PlayerInfo.html",
        controller: "spillerinfoCtril"
    }).
    when("/Selskap/", { 
        templateUrl: "Layouts/CompanyList.html",
        controller: "companyCtrl"
    }).
    when("/selskapinfo/:selskapID", { 
        templateUrl: "Layouts/CompanyInfo.html",
        controller: "companyInfoCtrl"
    }).  
    when("/spillersammenlign/:brukerID/:compareID", {
        templateUrl: "Layouts/PlayerCompare.html",
        controller: "playerCompareCtrl"
    }). 
    when("/RedigerBruker/:brukerID", {
        templateUrl: "Layouts/EditUser.html",
        controller: "brukereditCtrl"
    }). 
    when("/spillmatrise/:spillID", {
        templateUrl: "Layouts/Spillmatrise.html",
        controller: "spillmatriseCtrl"
    }).
    when("/facebooklog/:codeID", {
        templateUrl: "Layouts/FacebookLogin.html",
        controller: "facebookLoginCtrl"
    }). 
    when("/kalender/:brukerID", {
        templateUrl: "Layouts/Calendar.html",
        controller: "calendarCtrl"
    }).            
    otherwise({
        templateUrl : "Layouts/Frontpage.html", 
        controller: "frontpageCtrl"       
    });
}]);



