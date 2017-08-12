(function() {
    'use strict';

    angular
        .module('majidiIigApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('news', {
            parent: 'entity',
            url: '/news?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'News'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/news/news.html',
                    controller: 'NewsController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('news-detail', {
            parent: 'news',
            url: '/news/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'News'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/news/news-detail.html',
                    controller: 'NewsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'News', function($stateParams, News) {
                    return News.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'news',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('news-detail.edit', {
            parent: 'news-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/news/news-dialog.html',
                    controller: 'NewsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['News', function(News) {
                            return News.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('news.new', {
            parent: 'news',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/news/news-dialog.html',
                    controller: 'NewsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                newsID: null,
                                newsTitle: null,
                                newsDescription: null,
                                newsBody: null,
                                newsKeywords: null,
                                newsCategory: null,
                                newsUrl: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('news', null, { reload: 'news' });
                }, function() {
                    $state.go('news');
                });
            }]
        })
        .state('news.edit', {
            parent: 'news',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/news/news-dialog.html',
                    controller: 'NewsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['News', function(News) {
                            return News.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('news', null, { reload: 'news' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('news.delete', {
            parent: 'news',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/news/news-delete-dialog.html',
                    controller: 'NewsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['News', function(News) {
                            return News.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('news', null, { reload: 'news' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
