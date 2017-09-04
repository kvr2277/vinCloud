app.controller('homeCtrl', ['$scope','$rootScope',  function($scope, $rootScope) {
	  $scope.modalShown = false;
	  $scope.showSection = false;
	  
	  $scope.toggleModal = function() {
	    $scope.modalShown = !$scope.modalShown;	    
	  };
	  
	  $scope.showSectionFn = function() {
		    $scope.showSection = true;	    
		  };
	  
	  $scope.showS3Fn = function() {
		  $rootScope.$emit('childEmit', true);
	  };
	 
}]);

app.controller('s3Ctrl', [ '$scope', '$http', '$rootScope','$timeout',
		function($scope, $http, $rootScope, $timeout) {
	

			$scope.s3DefaultImageSrc = '/images/s3_image.png';
			$scope.showS3 = false;
			$scope.s3Msg = "";
			 
			 $rootScope.$on('childEmit', function(event, data) {
				 $scope.showS3 = data;	
			});
			 
			$scope.uploadFile = function() {

				var file = $scope.myFile;

				console.log('file is ');
				console.dir(file);

				//var uploadUrl = "/uploadAndRetrieveFromLocalMachine";
				var uploadUrl = "/uploadToS3";
				var fd = new FormData();
				var resp = null;
				fd.append('file', file);
				
				var mno = $http.post(uploadUrl, fd, {
					transformRequest : angular.identity,
					transformResponse: angular.identity,
					headers : {
						'Content-Type' : undefined
					}
				}).success(function(response) {	
					$scope.s3Msg = "successfully uploaded to S3";					
					$scope.imageSrc = response;
					alert("successfully uploaded to S3");					
				}).error(function() {				
					alert("something went wrong");
				});
			};
}]);



app.directive('modalDialog', function() {
	  return {
	    restrict: 'E',
	    scope: {
	      show: '='
	    },
	    replace: true, // Replace with the template below
	    transclude: true, // we want to insert custom content inside the directive
	    link: function(scope, element, attrs) {
	      scope.dialogStyle = {};
	      if (attrs.width)
	        scope.dialogStyle.width = attrs.width;
	      if (attrs.height)
	        scope.dialogStyle.height = attrs.height;
	      scope.hideModal = function() {
	        scope.show = false;
	      };
	    },
	    template: "<div class='ng-modal' ng-show='show'><div class='ng-modal-overlay' ng-click='hideModal()'></div><div class='ng-modal-dialog' ng-style='dialogStyle'><div class='ng-modal-close' ng-click='hideModal()'>X</div><div class='ng-modal-dialog-content' ng-transclude></div></div></div>"
	  };
	});
