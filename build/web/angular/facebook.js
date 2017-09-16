/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

  function facebookLoadin($scope,$http, pageData){
        
        var js,
        id = 'facebook-jssdk',
        ref = document.getElementsByTagName('script')[0];

        if (document.getElementById(id)) {
        return;
        }

        js = document.createElement('script');
        js.id = id;
        js.async = true;
        js.src = "//connect.facebook.net/en_US/all.js";

        ref.parentNode.insertBefore(js, ref); 
        
        window.fbAsyncInit = function () {
                FB.init({
                    appId: '941343709256136',
                    xfbml: true,
                    version: 'v2.4'
                });
                facebookInit($scope,$http, pageData);
         };
        
        
    }
    
    





    function facebookInit($scope,$http, pageData) {

                FB.getLoginStatus(function (response) {
                    if (response.status === 'connected') {
                        // the user is logged in and has authenticated your
                        // app, and response.authResponse supplies
                        // the user's ID, a valid access token, a signed
                        // request, and the time the access token 
                        // and signed request each expire
                        var uid = response.authResponse.userID;
                        var accessToken = response.authResponse.accessToken;
                        //alert(accessToken);
      

                       $http({
                                method  : 'POST',
                                url     : '/Boardgame/Facebook/Namecheck',
                                data    : $.param({ uid: uid, accessToken: accessToken}),  // pass in data as strings
                                headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
                        })
                        .success(function(data){            
                                if(data == "SUCCESS"){
                                    loadPageMenu($scope, $http, pageData, pageData.pageFormat);
                                }
                        });  
                          
                            //form.submit();
                        
                    } else if (response.status === 'not_authorized') {
                        // the user is logged in to Facebook, 
                        // but has not authenticated your app
                        document.getElementById('errorMessageRed').innerHTML = "Du har ikke autentisert siden hos Facebook. Autentisert siden hos Facebook og forsøk igjen.";

                    } else {
                        // the user isn't logged in to Facebook.
                        document.getElementById('errorMessageRed').innerHTML = "Du er ikke logget inn på Facebook.";
                    }
                });



    }


