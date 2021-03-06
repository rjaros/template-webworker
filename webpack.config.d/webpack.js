if (config.entry.main[0].endsWith("frontend.js")) {
    config.resolve.modules.push("../../processedResources/frontend/main");
} else {
    config.resolve.fallback = {
        "fs": false,
        "path": false,
        "crypto": false
    };
}

if (config.devServer) {
    config.devServer.hot = true;
    config.devtool = 'eval-cheap-source-map';
} else {
    config.devtool = undefined;
}

// disable bundle size warning
config.performance = {
    assetFilter: function (assetFilename) {
      return !assetFilename.endsWith('.js');
    },
};
