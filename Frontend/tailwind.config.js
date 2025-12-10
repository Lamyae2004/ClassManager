/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}", // tous tes fichiers React
    "./public/index.html",         // index.html principal
    "./node_modules/@shadcn/ui/**/*.{js,ts,jsx,tsx}" 
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
