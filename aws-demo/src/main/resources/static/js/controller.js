app.directive('fileModel', [ '$parse', function($parse) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var model = $parse(attrs.fileModel);
			var modelSetter = model.assign;

			element.bind('change', function() {
				scope.$apply(function() {
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
} ]);

app.service('fileUpload', [ '$http', function($http) {

	var resp = null;
	this.uploadFileToUrl = function(file, uploadUrl) {
		alert(2);
		var fd = new FormData();

		fd.append('file', file);
		$http.post(uploadUrl, fd, {
			transformRequest : angular.identity,
			headers : {
				'Content-Type' : undefined
			}
		}).success(function(response) {
			alert(3);
			resp = 'abc';
			return resp;
		}).error(function() {
		});
		alert("resp " + resp);
	}

} ]);

app.controller('myCtrl', [ '$scope', '$http', 'fileUpload',
		function($scope, $http, fileUpload) {

			$scope.myImgSrc = '/images/images.jpeg';

			$scope.uploadFile = function() {

				var file = $scope.myFile;

				console.log('file is ');
				console.dir(file);

				var uploadUrl = "/image";
				
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
					
					resp = 'abc';
					
					$scope.imageSrc = response;
					
				}).error(function() {
				
					
				});

				$scope.name = 'good';
			};
		} ]);
