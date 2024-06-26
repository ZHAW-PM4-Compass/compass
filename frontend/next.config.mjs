import path from 'path';
import { fileURLToPath } from 'url';
import withBundleAnalyzer from '@next/bundle-analyzer';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const bundleAnalyzer = withBundleAnalyzer({
  enabled: process.env.ANALYZE === 'true',
});

const isTest = process.env.NODE_ENV === 'test';

/** @type {import('next').NextConfig} */
const nextConfig = bundleAnalyzer({
  eslint: {
    dirs: ['.'],
  },
  poweredByHeader: false,
  reactStrictMode: true,
  experimental: {
    serverComponentsExternalPackages: ['pino'],
  },
  webpack: (config, { dev, isServer }) => {
    config.externals.push({
      bufferutil: 'bufferutil',
      'utf-8-validate': 'utf-8-validate',
    });

    if (isTest) {
      config.module.rules.push({
        test: /\.(js|jsx|ts|tsx)$/,
        include: [path.resolve(__dirname, 'src'), path.resolve(__dirname, 'pages')],
        use: {
          loader: 'babel-loader',
          options: {
            presets: [['next/babel']],
            plugins: ['babel-plugin-istanbul'],
          },
        },
      });
    }

    return config;
  },
});

export default nextConfig;
