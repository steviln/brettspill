/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function loadPageMenu($scope, $http, pageData, pageID, $window){
    $http.get("/Boardgame/Ajaxdest/GetMenu/" + pageID + "/" + new Date().getTime()).then(function(response){
        $scope.menypunkter = response.data.meny; 
        prepPage($window, $scope);
    });
    
    
}

function loginStatusChanged($scope, $http, statusText, pageData, $window){
        loadPageMenu($scope, $http, pageData, "");
        if(statusText == "SUCCESS"){            
            document.getElementById("errorLogin").style.display = "none";
            $window.location.href = "/Boardgame/";
        }
        else if(statusText == "FAILURE"){            
            document.getElementById("errorLogin").style.display = "block";
        }
        else{
            document.getElementById("errorLogin").style.display = "none";
        }   
}

function prepPage($window, $scope){
    if($window.innerWidth <= 450){
        $window.onscroll = function() {
            if($window.scrollY < document.getElementById("topMenu").clientHeight){
                document.getElementById("menuButton").style.display = "none";
            }
            else{
                document.getElementById("menuButton").style.display = "block";
            }
        };
        prepClick($window, $scope);
    }
    
}

function prepClick($window, $scope){
    if($window.innerWidth <= 450 && $scope.menypunkter){
        var menheight = (($scope.menypunkter.length * 64) + 94);
        window.scrollTo(0,menheight);
    }
    
}

function goToTop(){
    window.scrollTo(0,0);
}



