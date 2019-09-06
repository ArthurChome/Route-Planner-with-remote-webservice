$("#to, #from").autocomplete({
    source: stationNameSuggestion,
    minLength: 2
 });

 $("#station").autocomplete({
     source: stationNameSuggestion,
     minLength: 2
  });

 function stationNameSuggestion(request, response){
       $.ajax(jsRoutes.controllers.StationController.getStationNames(request.term))
           .done(function(data){
                  response($.map(data, function(item){
                        return { label: item, value: item}
                  }))
           })
           .fail(function(error){console.log(error)});
 }