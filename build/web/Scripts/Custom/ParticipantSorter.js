

    
    var dragit = null, prev = null;
    var relX, relY;

    function mouse_it_down(e) {
      
        var a = e.target;
        prev = $(a.parentElement);
        var dragElement = document.createElement("div");
        dragElement.className = "deltakelseZone";
        dragElement.innerHTML = a.parentElement.innerHTML;
        dragit = $(dragElement);
        dragit.disableSelection();
        prev.disableSelection();
        var maloffset = prev.offset();
        relX = e.pageX - maloffset.left;
        relY = e.pageY - maloffset.top;
        
     
        if(prev == null){
            //alert("prev is null");            
        }
        else{
            //alert(prev.attr("class"));
        }
        prev.css("display", "none");
        dragit.css("position", "absolute");
        dragit.css("z-index", "1000");
        dragit.css("top", (e.pageY - relY) + "px");
        dragit.css("left", (e.pageX - relX) + "px");
        dragit.appendTo($("body"));
        

    }

    function oppmus(e, $scope) {
       
        if (dragit != null) {
           
            var draguse = dragit;
            dragit = null;
            draguse.removeAttr('style');
            draguse.remove();

            var under = null;
            var offit;
            var delzone = document.getElementsByClassName("deltakelseZone");
            
            var dragdata = prev.find('.redigerig').attr('value');
            var enddata = "";
            
            for(var ax = 0; ax < delzone.length; ax++){
                var dragcheck = $(delzone.item(ax));
                if (!dragcheck.is(draguse)) {
                    offit = dragcheck.offset();
                    if (e.pageX > offit.left && e.pageX < (offit.left + dragcheck.width()) && e.pageY > offit.top && e.pageY < (offit.top + dragcheck.height())) {
                        under = dragcheck;
                    }
                }
            }
            
            if(under == null){
                //alert("under is null");               
            }
            else{
                enddata = under.find('.redigerig').attr('value');
                //alert("under is not null");
            }
            
            var bunnrangering = 0;
            for(var x = 0; x < $scope.sesjonen.deltakelser.length; x++){
                if($scope.sesjonen.deltakelser[x].spillerID > 0)
                    bunnrangering++;
            }

            if (under == null) {
                //alert(prev.attr('id') + " " + draguse.attr('id'));
                //prev.before(draguse);
            }
            else {
                var movedata = null;
                for(var x = 0; x < $scope.sesjonen.deltakelser.length; x++){
                    if($scope.sesjonen.deltakelser[x].redigerid == dragdata){
                        movedata = $scope.sesjonen.deltakelser[x];  
                        $scope.sesjonen.deltakelser.splice(x,1);
                    }                    
                }
                for(var x = 0; x < $scope.sesjonen.deltakelser.length; x++){
                    if($scope.sesjonen.deltakelser[x].redigerid == enddata && movedata != null){
                        movedata.posisjon = $scope.sesjonen.deltakelser[x].posisjon;
                        if(x == 0)
                            movedata.posisjon = 1;
                        $scope.sesjonen.deltakelser.splice(x,0,movedata);
                        movedata = null;

                    }  
                    else if(movedata == null && $scope.sesjonen.deltakelser[x].posisjon < bunnrangering){                       
                        $scope.sesjonen.deltakelser[x].posisjon++;
                    }
                }
                
            }
           $scope.$apply();
            //prev.find('.dragField').on("mousedown", function (e) { mouse_it_down(e, $(this)); });
            prev.css("display", "block");
            prev = null;

        }
    }

    function museflytt(e) {
        if (dragit != null) {
            dragit.css("top", (e.pageY - relY) + "px");
            dragit.css("left", (e.pageX - relX) + "px");
        }
    }








    var inputUpdater;

    function visUpdater() {
        var bruk = document.getElementById('spillUpdaterDiv');
        if(document.body.clientWidth < 800){
            $('#spillUpdaterDiv').css("top", ($(document).scrollTop()) + 'px');
            $('#spillUpdaterDiv').css("left", '0px');
            $('#spillUpdaterDiv').css("display","block");           
        }
        else{
            $('#spillUpdaterDiv').css("top", ($(document).scrollTop() + 200) + 'px');
            $('#spillUpdaterDiv').css("display","block");
        }
        document.getElementById("redigerid").value = redigerid;        
    }
    
    function visUpdaterSpiller(redigerid) {
        var bruk = document.getElementById('personUpdaterDiv');
        if(document.body.clientWidth < 800){
            $('#personUpdaterDiv').css("top", ($(document).scrollTop() - document.getElementById("menBount").clientHeight) + 'px');
            $('#personUpdaterDiv').css("left", '0px');
            $('#personUpdaterDiv').css("display","block");           
        }
        else{
            $('#personUpdaterDiv').css("top", ($(document).scrollTop() + 200) + 'px');
            $('#personUpdaterDiv').css("display","block");
        }
        document.getElementById("redigerid").value = redigerid;
    }   
    
   function canelNyspiller() {
        $('#personUpdaterDiv').css("display", "none");
    }
    
    function addNyFabrikant($scope) {
        
        var firstvar = $('#thirdVariable').attr('value');
        var secondvar = "";
        $.ajax({
            type: 'GET',
            url: '/Boardgame/Ajaxdest/CreateCompany',
            data: { first: firstvar, second: secondvar },
            success: function (data) {
                var returnValues = data.split(";");
                $scope.spillet.opsjoner.push({navn:returnValues[1],id:returnValues[0]});
                $scope.spillet.selskapID = returnValues[0];
                $('#spillUpdaterDiv').css("display", "none");
                $scope.$apply();
            }
        });
    }
    
    function addNySpiller($scope) {
        
        var firstvar = document.getElementById('firstVariable').value;
        var secondvar = document.getElementById('secondVariable').value;
        var redigerene = document.getElementById('redigerid').value;
        $.ajax({
            type: 'GET',
            url: '/Boardgame/Ajaxdest/CreateNewPlayer',
            data: { first: stringForSend(firstvar), second: stringForSend(secondvar), redigerid: redigerene },
            success: function (data) {
                var returnValues = data.split(";");
                $scope.sesjonen.spillere.push({navn:returnValues[1],id:returnValues[0]});
                var redid = returnValues[2];
                for(var y = 0; y < $scope.sesjonen.deltakelser.length; y++){ 
                    if($scope.sesjonen.deltakelser[y].redigerid == redid){                        
                        $scope.sesjonen.deltakelser[y].spillerID = returnValues[0];
                    }
                }
                document.getElementById("personUpdaterDiv").style.display = "none";
                document.getElementById('firstVariable').value = "";
                document.getElementById('secondVariable').value = "";
                $scope.$apply();
            }
        });
    }    

    function rangerSpill($scope) {
        // var sendval = $(this).val(); 
        //var spillval = $('#modspillID').val();
        //$.ajax({
        //    type: 'GET',
        //    url: '/Ajax/RateGame/',
        //    data: { first: sendval, second: spillval },
        //    success: function (data) {
                
        //    }
        //});
    }
    
    function sorterDeltakelser($scope, $filter) {
      
        $scope.sesjonen.deltakelser = $filter('orderBy')($scope.sesjonen.deltakelser,'-1*poeng');
        
        var teller = 1;
        angular.forEach($scope.sesjonen.deltakelser, function(value, key){
           value.posisjon = teller;
           teller++;
        });
        
        var lastpoeng = null;
        var lastplace = null;
        for(var x = $scope.sesjonen.deltakelser.length -1; x >= 0; x--){
            if($scope.sesjonen.deltakelser[x].spillerID > 0){
                if($scope.sesjonen.deltakelser[x].poeng == lastpoeng){
                    $scope.sesjonen.deltakelser[x].posisjon = lastplace;
                }
                lastpoeng = $scope.sesjonen.deltakelser[x].poeng;
                lastplace = $scope.sesjonen.deltakelser[x].posisjon;
            }            
        }
    }
    
    function sorterPosDeltakelser($scope, $filter) {
        for(var x = 0; x < $scope.sesjonen.deltakelser.length; x++){    
            $scope.sesjonen.deltakelser[x].posisjon = x + 1;
        }
    }
    
    
    function stringForSend(sString){
        
        sString = sString.replace("æ","1ae1");
        sString = sString.replace("ø","1oe1");
        sString = sString.replace("å","1aa1");
        sString = sString.replace("Æ","1AE1");
        sString = sString.replace("Ø","1OE1");
        sString = sString.replace("Å","1AA1");
        
        return sString;
    }
    
    function lageGraf($scope){
        var graf = document.getElementById("spillerevnegraf");
        var antallspillinger = $scope.modellen.spillinger.length;
        var spillingene = $scope.modellen.spillinger;
        if(antallspillinger > 15){
            graf.style.display = "block";
            var grafpad = 10;
            var areaheight = graf.clientHeight - (grafpad * 2);
            var areawidth = graf.clientWidth - (grafpad * 2);
            graf.width = areawidth + (grafpad * 2);
            graf.height = areaheight + (grafpad * 2);
            var between = 5;                           
            var antspill = Math.round(antallspillinger / (areawidth / between));
            if(antspill <= 1){
                antspill = 1;
                between = Math.round(areawidth / (antallspillinger));
            }

            var startcount = 0, tempscore = 0, lastline = -1;
            var paint = graf.getContext("2d");
           
            for(var i = 1; i < spillingene.length; i++){                
                startcount++;
                tempscore += spillingene[i].rank;
                if(startcount == antspill || i == (spillingene.length - 1)){
                    var countscore = Math.round(tempscore / startcount);
                    startcount = 0;
                    tempscore = 0;  

                    var heightpos = Math.round(areaheight - (areaheight * (countscore / 100)));
                    if(lastline >= 0){
                        paint.moveTo(Math.round(((i - antspill) * between) + grafpad), lastline + grafpad);
                        paint.lineTo(Math.round((i * between) + grafpad), heightpos + grafpad);
                        paint.stroke();
                    }
                    lastline = heightpos;            
                   
 
                }
            }
        }
        
    }
    
    function labelsjekk(tekstfelt){
        if(tekstfelt.value == 0){
            tekstfelt.value = "";
        }
    }
    
    function orderPlayerChoices($scope, pagewidth, setspillere){
        var playerlengde = setspillere.length;
        var deler = 5;
        if(pagewidth < 451){
            deler = 2;
        }
        var mainarray = [];
        var currarray;
        var delepunkt = playerlengde / deler;
        for(var v = 0; v < playerlengde; v++){
            var dekd = Math.floor(v % delepunkt);
            if(dekd == 0){
                currarray = [];
                mainarray.push(currarray);
            }
            currarray.push(setspillere[v]);
        }
        $scope.spillet.selspillarray = mainarray;
    }
    
    function hentJavaAjax($scope, $window, $http, parames, url, myFunc){
            $http({
                method  : 'POST',
                url     : url,
                data    : parames,  // pass in data as strings
                headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
            
            })
            .success(function(data){
                myFunc($scope, data);
            });        
        
    }
    
    function initCalendar($scope, $http, $routeParams, $window){
        $scope.lastEndret = null;
        $scope.firstClick = null;
        $scope.setDateStatus = null;
        $scope.endreKalenderDag = function(dagen,tilstatus){
            dagen.status = tilstatus;
            $scope.lastEndret.inclass = "inclasshidden";
            hentJavaAjax($scope, $window, $http, $.param(dagen), "/Boardgame/Ajaxdest/EndreDagKalender", endreUkedagDag);
        };
        $scope.lukkDayen = function(dagen, $event){
            $scope.lastEndret.inclass = "inclasshidden"; 
            $event.stopPropagation();
        };
        $scope.settSynlig = function(dagen){
            if($scope.lastEndret != null){ $scope.lastEndret.inclass = "inclasshidden"; }
            if($scope.modellen.userID == $scope.modellen.eierID){
                $scope.lastEndret = dagen;
                dagen.inclass = "inclasshow";
            }
        };
        $scope.kalenderMusNed = function(klikkcelle){
            if($scope.setDateStatus != null){
                $scope.firstClick = klikkcelle;
            }
        };
        $scope.kalenderMusOpp = function(klikkcelle){
            if($scope.firstClick != null && $scope.setDateStatus != null){
                var sendekjede = { fradato : $scope.firstClick.datestring, tildato: klikkcelle.datestring, brukerID : $scope.modellen.userID, datoSetID: $scope.setDateStatus.id, forsteklikkID: $scope.firstClick.id, andreklikkID: klikkcelle.id };
                hentJavaAjax($scope, $window, $http, $.param(sendekjede), "/Boardgame/Ajaxdest/LagKalenderKjede", settNyKalenderKjede);
            }           
        };
        $scope.kalenderMusUt = function(){
            $scope.firstClick = null;
        };
        $scope.valgTypeKalender = function(typevalg){
            if($scope.setDateStatus != null){
                $scope.setDateStatus.omklasse = "calselunselect";
            }
            $scope.setDateStatus = typevalg;
            $scope.setDateStatus.omklasse = "calseselect";
        };
        $http.get("/Boardgame/Ajaxdest/Calendar/" + $routeParams.brukerID).then(function(response){
            $scope.modellen = response.data;
        });        
        
    }
    
    function settNyKalenderKjede($scope, data){
        alert(data);
        
        
    }
    
    function endreUkedagDag($scope, data){
        var strengdel = data.split(":");
        $scope.modellen.ukedager[strengdel[0]].dagclass = "ukedag ukedag" + strengdel[1];
    }
    
    function nullReturnAjax($scope, data){}
    

    
  