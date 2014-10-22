var SLIDE_CONFIG = {
  // Slide settings
  settings: {
    title: 'Android Wear: Who\'s Next',
    subtitle: 'My first few weeks with the Moto360 <br />http://reisz-whos-next.appspot.com',
    useBuilds: true, // Default: true. False will turn off slide animation builds.
    usePrettify: true, // Default: true
    enableSlideAreas: true, // Default: true. False turns off the click areas on either slide of the slides.
    enableTouch: true, // Default: true. If touch support should enabled. Note: the device must support touch.
    //analytics: 'UA-XXXXXXXX-1', // TODO: Using this breaks GA for some reason (probably requirejs). Update your tracking code in template.html instead.
    favIcon: 'images/google_developers_logo_tiny.png',
    fonts: [
      'Open Sans:regular,semibold,italic,italicsemibold',
      'Inconsolata'
    ],
    //theme: ['mytheme'], // Add your own custom themes or styles in /theme/css. Leave off the .css extension.
  },

  // Author information
  presenters: [{
    name: 'Wesley Reisz @wesreisz',
    company: '',
    github: 'https://github.com/wesreisz/Android-Wear-Whos-Next',
    twitter: '@wesreisz',
    email: 'wes@wesleyreisz.com',
    slideUrl: "http://android-wear-whos-next.appspot.com",
    www: 'http://www.wesleyreisz.com'
  }/*, {
    name: 'Second Name',
    company: 'Job Title, Google',
    gplus: 'http://plus.google.com/1234567890',
    twitter: '@yourhandle',
    www: 'http://www.you.com',
    github: 'http://github.com/you'
  }*/]
};

