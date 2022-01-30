var timestamp = 1643132540*1000;
var d = new Date(timestamp);
var dO = {year: 'numeric', month: 'numeric', day: 'numeric'};
var tO = {hour: 'numeric', minute: 'numeric', second: 'numeric'};
var nl = navigator.languages;
console.log(nl);
console.log(new Intl.DateTimeFormat(nl, dO).format(d));
console.log(new Intl.DateTimeFormat(nl, tO).format(d));