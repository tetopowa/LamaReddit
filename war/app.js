'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.view1',
    'myApp.view2',
    'myApp.version'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!');

    $routeProvider.otherwise({redirectTo: '/view1'});
}]).

controller('Logger', function($rootScope, $scope, $window) {

    $rootScope.signedIn = false;

        //console.log(googleUser.getBasicProfile());
    function onSignIn(googleUser){
        $rootScope.signedIn = true;
        let profile =  googleUser.getBasicProfile();
        $rootScope.user = {
            'id': profile.getId(),
            'author_name': profile.getName(),
            'author_mail': profile.getEmail()
        };
    }

    $window.onSignIn = onSignIn;

    function signOut() {
        let auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut().then(function () {
            console.log('User signed out.');
            $rootScope.signedIn = false;
        });
    }
    $window.signOut = signOut;








});




