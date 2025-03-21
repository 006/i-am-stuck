import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  experimental: {
    // missingSuspenseWithCSRBailout: false,
    nodeMiddleware: true, // Enable Node.js middleware
  },
  output: "standalone",
  webpack: (config) => {
    config.resolve.fallback = { fs: false, path: false };
    return config;
  },
};

export default nextConfig;
