/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


mainapp.controller("mainControl", function($scope, $http, pageData, $anchorScroll, $location, $window){

    loadPageMenu($scope, $http, pageData, pageData.pageFormat, $window, $anchorScroll, $location);

    //facebookLoadin($scope,$http, pageData);
    
});
mainapp.controller("calendarCtrl", function($scope, $routeParams, $http, $window){
    initCalendar($scope, $http, $routeParams, $window);
});
mainapp.controller("facebookLoginCtrl", function($scope, $routeParams, $http, pageData, $window){
    if($routeParams.codeID == 1){       
        $scope.feedback = "Din bruker ble ikke funnet i databasen. Administrator må ordne dette manuelt.";
    }
    else{
        $scope.feedback = "Det var noe feil med responsen fra Facebook, og du ble ikke logget inn.";
    }
    prepClick($window, $scope);
    //loadPageMenu($scope, $http, pageData, pageData.pageFormat);
    //facebookLoadin($scope,$http, pageData);
    
});
mainapp.controller("playerCompareCtrl", function($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/ComparePlayers/" + $routeParams.brukerID + "/" + $routeParams.compareID + "/").then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });
});
mainapp.controller("brukereditCtrl", function ($scope, $routeParams, $http, $window){
   
     $scope.rediger = function(){
        $.ajax({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/CreateUpdateUser',
            data: { fornavn: stringForSend($scope.modellen.fornavn), etternavn: stringForSend($scope.modellen.etternavn), epost: stringForSend($scope.modellen.epost), passord: stringForSend($scope.modellen.passord), passordto: stringForSend($scope.modellen.passordto), id: $scope.modellen.id, ignore: $scope.modellen.ignore, status: $scope.modellen.status },
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){            
            $window.location.href = "/Boardgame/#/RedigerBruker/" + data;
        });  
     };  
     $scope.sjekked = function(){
          if($scope.modellen.ignore == 1)
              $scope.modellen.ignore = 0;
          else
              $scope.modellen.ignore = 1;
     };
     $scope.sjekkedUser = function(myranke){
          if(myranke.eier == 1)
              myranke.eier = 0;
          else
              myranke.eier = 1;
          hentJavaAjax($scope, $window, $http, $.param(myranke), "/Boardgame/Ajaxdest/SetGameOwner", nullReturnAjax);
     };
     $scope.spillchange = function(rag){
          hentJavaAjax($scope, $window, $http, $.param(rag), "/Boardgame/Ajaxdest/RankGame", nullReturnAjax);
     };
     
     $http.get("/Boardgame/Ajaxdest/GetUserDetails/" + $routeParams.brukerID).then(function(response){            
            $scope.modellen = response.data;
            prepClick($window, $scope);
     });
    
});
mainapp.controller("frontpageCtrl", function ($scope, $routeParams, $http, $window) {    
    $http.get("/Boardgame/Ajaxdest/GetFrontpage/").then(function(response){      
       $scope.modellen = response.data;
       prepClick($window, $scope);
    });
    
}); 
mainapp.controller("companyCtrl", function($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/GetCompanyList/").then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });
});
mainapp.controller("companyInfoCtrl", function($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/GetCompanyInfo/" + $routeParams.selskapID).then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });
});
mainapp.controller("spillerinfoCtril", function($scope, $http, $routeParams, pageData, $window){
    $http.get("/Boardgame/Ajaxdest/GetPlayerProfile/" + $routeParams.spillerID).then(function(response){
        $scope.modellen = response.data;
        lageGraf($scope);
        prepClick($window, $scope);
    });
});
mainapp.controller("sessionlistCtrl", function ($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/GetSessionList/").then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });   
}); 
mainapp.controller("spillerlisteCtrl", function ($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/GetSpillerliste").then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });
});
mainapp.controller("sessionVisCtrl", function ($scope, $routeParams, $http, $filter, $window){
    $http.get("/Boardgame/Ajaxdest/GetSessionVis/" + $routeParams.sesjonID).then(function(response){
       $scope.modellen = response.data;
       angular.forEach($scope.modellen.deltakelser, function (delt) {
                delt.poeng = parseFloat(delt.poeng);
        });
        $scope.modellen.deltakelser = $filter('orderBy')($scope.modellen.deltakelser,'posisjon');
        prepClick($window, $scope);
    });
});
mainapp.controller("gameslistCtrl", function ($scope, $routeParams, $http, $window){

    $http.get("/Boardgame/Ajaxdest/GetGamesList").then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
    });
});
mainapp.controller("gameinfoCtrl", function ($scope, $routeParams, $http, $window){
    $http.get("/Boardgame/Ajaxdest/GetGameInfo/" + $routeParams.gameID).then(function(response){
        $scope.modellen = response.data;
        prepClick($window, $scope);
        if($scope.modellen.ranken >= 0){
            document.getElementById("spillRangeringDiv").style.display = "block";
        }
        else{            
            document.getElementById("spillRangeringDiv").style.display = "none";
        }
        if($scope.modellen.fraks.length > 0){
            document.getElementById("matriselink").style.display = "block";
        }
        else{            
            document.getElementById("matriselink").style.display = "none";
        }        
        $scope.endretrangering = function(nyrating){
            $scope.modellen.ranken = nyrating;
            hentJavaAjax($scope, $window, $http, $.param($scope.modellen), "/Boardgame/Ajaxdest/RankGame", nullReturnAjax);
        }
    });
});
mainapp.controller("editgameCtrl", function($scope, $routeParams, $http, $window){
     $scope.rediger = function(){
        $http({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/CreateUpdateGame',
            data    : $.param($scope.spillet),  // pass in data as strings
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){
            $window.location.href = "/Boardgame/#/Spillinfo/" + data;
        });
    };   
    
    $scope.sendSpiller = function(){
        addNyFabrikant($scope);
    };
    
    $scope.selecSpiller = function(para){
        if(para.divclass == "unselec"){
            $scope.selecspillere.push(para);
            para.divclass = "selec";
        }else{ 
            $scope.selecspillere.splice($scope.selecspillere.indexOf(para),1);
            para.divclass = "unselec"; 
        }      
    };
    $scope.loggSpillPreset = function(){
        var prestrent = "";
        for(var v = 0; v < $scope.selecspillere.length; v++){
            if(v > 0){ prestrent += ":";}
            prestrent += $scope.selecspillere[v].id;
        }
        $window.location.href = "/Boardgame/#redigerSesjon/0/" + $scope.spillet.id + "/" + prestrent;
    }
    
    $scope.velgSpillere = function(){
        $http.get("/Boardgame/Ajaxdest/GetPlayersForPreset").then(function(response){
            orderPlayerChoices($scope, $window.innerWidth, response.data.setspillere);
            $scope.selecspillere = [];
            $scope.spillet.setspillere = response.data.setspillere;
            document.getElementById("playerChooser").style.display = "block";
        }); 
    }
    
    
    $scope.kanseller = function(e){
        $('#spillUpdaterDiv').css("display", "none");
        document.getElementById("playerChooser").style.display = "none";
    };
    
    $scope.$on("$destroy", function(){
                $('#spillUpdaterDiv').css("display", "none");
                document.getElementById("playerChooser").style.display = "none";
    });
    
    $http.get("/Boardgame/Ajaxdest/EditGameInfo/" + $routeParams.gameID).then(function(response){
        $scope.spillet = response.data;
        $scope.selskapID = response.data.selskapID; 
        prepClick($window, $scope);
    });   
});
mainapp.controller("editfactionCtrl", function($scope, $routeParams, $http, $window){
     $scope.rediger = function(){
        $http({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/CreateUpdateFaction',
            data    : $.param($scope.fraksjonen),  // pass in data as strings
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){
            $window.location.href = "/Boardgame/#/redigerSpill/" + data;
        });
    };    
    $http.get("/Boardgame/Ajaxdest/EditFactionInfo/" + $routeParams.frakID + "/" + $routeParams.spillID).then(function(response){
        $scope.fraksjonen = response.data;  
        prepClick($window, $scope);
    });   
});
mainapp.controller("editscenarioCtrl", function($scope, $routeParams, $http, $window){
     $scope.rediger = function(){
        $http({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/CreateUpdateScenario',
            data    : $.param($scope.scenario),  // pass in data as strings
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){
            $window.location.href = "/Boardgame/#/redigerSpill/" + data;
        });
    };    
    $http.get("/Boardgame/Ajaxdest/EditScenarioInfo/" + $routeParams.scenID + "/" + $routeParams.spillID).then(function(response){
        $scope.scenario = response.data;  
        prepClick($window, $scope);
    });   
});
mainapp.controller("loginCtrl", function($scope, $http, $window, pageData){
    $scope.submit = function(){
        $http({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/DoLogin',
            data    : $.param($scope.user),  // pass in data as strings
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){
            loginStatusChanged($scope, $http, data, pageData, $window);
            prepClick($window, $scope);
        });
    };
});
mainapp.controller("logoutCtrl", function($scope, $http, $window, pageData){

    $http.get("/Boardgame/Ajaxdest/Logout/").then(function(response){
        loadPageMenu($scope, $http, pageData, "");
        $window.location.href = "/Boardgame/";
    }); 

});
mainapp.controller("spillmatriseCtrl", function($scope, $http, $routeParams, $window, pageData){
    $http.get("/Boardgame/Ajaxdest/GetGameMatrix/" + $routeParams.spillID).then(function(response){
            $scope.modellen = response.data;
            prepClick($window, $scope);
    }); 

});
mainapp.controller("gameloggerCtrl", function($scope, $routeParams, $http, $window, $filter, pageData){
    $scope.rediger = function(){
        document.getElementById("submitknappsesjon").style.display = "none";
        $http({
            method  : 'POST',
            url     : '/Boardgame/Ajaxdest/LogGame',
            data    : $.param($scope.sesjonen),  // pass in data as strings
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
        })
        .success(function(data){
            $window.location.href = "/Boardgame/#/redigerSpill/" + data;
        });
    };
    
    $scope.sorter = function(){
        sorterDeltakelser($scope, $filter);        
    };    
    $scope.sorterPosisjon = function(){
        sorterPosDeltakelser($scope, $filter);
    }     
    $scope.sendEnNySpiller = function(){
        addNySpiller($scope);
    };
    $scope.visUpdaterSpilleren = function(brukID){
        visUpdaterSpiller(brukID);
    };
    

    
    $scope.kanselleren = function(e){
        document.getElementById("personUpdaterDiv").style.display = "none";
    };
    
    $scope.$on("$destroy", function(){
        document.getElementById("personUpdaterDiv").style.display = "none";
    });
    

    
    var presets = "";
    if($routeParams.presetsID){ presets = $routeParams.presetsID; }

    $http.get("/Boardgame/Ajaxdest/EditSession/" + $routeParams.sesjonID + "/" + $routeParams.spillID + "/" + presets).then(function(response){
          $scope.sesjonen = response.data;
          prepClick($window, $scope);
    });
    
    //document.addEventListener("mousemove", museflytt);
    //document.addEventListener("mouseup", oppmus);
    //document.addEventListener("mouseup", function(e){ oppmus(e, $scope);});

   
});

