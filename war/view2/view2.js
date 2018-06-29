'use strict';

angular.module('myApp.view2', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view2', {
    templateUrl: 'view2/view2.html',
    controller: 'View2Ctrl'
  });
}])

.controller('View2Ctrl', ['$rootScope', '$window','$http', '$scope', function($rootScope, $window, $http, $scope){

    let rootApi = 'https://1-dot-lamareddit-205114.appspot.com/_ah/api/messageendpoint/v1/';
    console.log($rootScope.user);
    getAllMyMessages();
    getAllMyLikedMessages();


    function getAllMyMessages() {
        let messages;
        $http.get(rootApi+'getUserMessage?userID='+$rootScope.user.id).then(function(response){
            messages = response.items;
            $scope.myMessages = response.data.items;
            //console.log(response.data.items);
        });
        return messages;
    }
    function getAllMyLikedMessages() {
        let messages;
        $http.get(rootApi+'getUserLikedMessage?userID='+$rootScope.user.id).then(function(response){
            messages = response.items;
            $scope.likedMessages = response.data.items;
        });
        return messages;
    }
}]);