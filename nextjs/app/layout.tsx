import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { IBM_Plex_Mono } from "next/font/google";
import "./globals.css";
import Script from 'next/script'
// import { preload } from 'react-dom';

const ibmPlex = IBM_Plex_Mono({
  weight: ["400"],
  variable: '--font-ibm-plex',
  subsets: ["latin"],
});

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "I Am Stuck",
  description: "I am stuck, need a pull",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  // preload("https://fonts.googleapis.com/icon?family=Material+Icons", { as: "style" })
  return (
    <html lang="en">
      <head>
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
      </head>
      <body className={`${geistSans.variable} ${geistMono.variable} ${ibmPlex.variable} antialiased`} >
        <Script src="/__ENV.js" strategy="beforeInteractive" />
        {children}
      </body>
    </html>
  );
}
