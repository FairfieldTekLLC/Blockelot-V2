/**
    * @package Ecobox Responsive HTML Template
    * 
    * Template Scripts
    * Created by Dan Fisher

    Custom JS
    
    1. Main Navigation
    2. Isotope
    3. Flickr
    4. FitVid (responsive video)
    5. Carousel
    -- Misc
*/

jQuery(function($) {

    /* ----------------------------------------------------------- */
    /*  1. Navigation
    /* ----------------------------------------------------------- */
    $(".flexnav").flexNav({
        'buttonSelector': ".navbar-toggle", // default menu button class name
        'animationSpeed': 250, // default for drop down animation speed
        'hoverIntent': true, // Change to true for use with hoverIntent plugin
        'hoverIntentTimeout': 100 // hoverIntent default timeout
    });

    // Affix (fixed navigation)
    $("#navbar").affix({
        offset: {
            top: 100,
            bottom: function() {
                return (this.bottom = $(".footer-wrap").outerHeight(true));
            }
        }
    });


    /* ----------------------------------------------------------- */
    /*  2. Isotope
    /* ----------------------------------------------------------- */

    (function() {

        // modified Isotope methods for gutters in masonry
        $.Isotope.prototype._getMasonryGutterColumns = function() {
            var gutter = this.options.masonry && this.options.masonry.gutterWidth || 0;
            containerWidth = this.element.width();

            this.masonry.columnWidth = this.options.masonry && this.options.masonry.columnWidth ||
                // or use the size of the first item
                this.$filteredAtoms.outerWidth(true) ||
                // if there's no items, use size of container
                containerWidth;

            this.masonry.columnWidth += gutter;

            this.masonry.cols = Math.floor((containerWidth + gutter) / this.masonry.columnWidth);
            this.masonry.cols = Math.max(this.masonry.cols, 1);
        };

        $.Isotope.prototype._masonryReset = function() {
            // layout-specific props
            this.masonry = {};
            // FIXME shouldn't have to call this again
            this._getMasonryGutterColumns();
            var i = this.masonry.cols;
            this.masonry.colYs = [];
            while (i--) {
                this.masonry.colYs.push(0);
            }
        };

        $.Isotope.prototype._masonryResizeChanged = function() {
            var prevSegments = this.masonry.cols;
            // update cols/rows
            this._getMasonryGutterColumns();
            // return if updated cols/rows is not equal to previous
            return (this.masonry.cols !== prevSegments);
        };


        // Set Gutter width
        var gutterSize;

        function getWindowWidth() {
            if ($(window).width() < 480) {
                gutterSize = 0;
            } else if ($(window).width() < 768) {
                gutterSize = 7;
            } else if ($(window).width() < 980) {
                gutterSize = 7;
            } else {
                gutterSize = 7;
            }
        }


        // Portfolio settings
        var $container = $(".gallery-list__grid");
        var $filter = $(".gallery-filter");

        $(window).smartresize(function() {
            getWindowWidth();
            $container.isotope({
                filter: "*",
                resizable: true,
                // set columnWidth to a percentage of container width
                masonry: {
                    gutterWidth: gutterSize
                }
            });
        });

        $container.imagesLoaded(function() {
            $(window).smartresize();
        });

        // Filter items when filter link is clicked
        $filter.find("a").click(function() {
            var selector = $(this).attr("data-filter");
            $filter.find("a").removeClass("current");
            $(this).addClass("current");
            $container.isotope({
                filter: selector,
                animationOptions: {
                    animationDuration: 750,
                    easing: "linear",
                    queue: false,
                }
            });
            return false;
        });

    })();


    /* ----------------------------------------------------------- */
    /*  3. Flickr
    /* ----------------------------------------------------------- */

    $("#flickr").jflickrfeed({
        limit: 15,
        qstrings: {
            id: "52617155@N08"
        },
        itemTemplate:
            '<li class="thumb"><a class="thumbnail" href="{{link}}" target="_blank"><img src="{{image_s}}" alt="{{title}}" /></a></li>'
    });


    /* ----------------------------------------------------------- */
    /*  4. FitVid
    /* ----------------------------------------------------------- */
    $(".screen-inner, .video").fitVids();


    /* ----------------------------------------------------------- */
    /*  5. Carousel
    /* ----------------------------------------------------------- */
    var owl = $("#team-carousel");

    owl.owlCarousel({
        items: 3, //10 items above 1000px browser width
        itemsDesktop: [1000, 3], //5 items between 1000px and 901px
        itemsDesktopSmall: [900, 2], // 3 items betweem 900px and 601px
        itemsTablet: [600, 2], //2 items between 600 and 0;
        itemsMobile: [480, 1], // itemsMobile disabled - inherit from itemsTablet option
        pagination: false

    });

    var owl4 = $("#carousel-four");

    owl4.owlCarousel({
        items: 4, //10 items above 1000px browser width
        itemsDesktop: [1000, 3], //5 items between 1000px and 901px
        itemsDesktopSmall: [900, 2], // 3 items betweem 900px and 601px
        itemsTablet: [600, 2], //2 items between 600 and 0;
        itemsMobile: [480, 1], // itemsMobile disabled - inherit from itemsTablet option
        pagination: false,
        navigationText: false

    });

    var owl5 = $("#carousel-five");

    owl5.owlCarousel({
        items: 5, //10 items above 1000px browser width
        itemsDesktop: [1000, 3], //5 items between 1000px and 901px
        itemsDesktopSmall: [900, 2], // 3 items betweem 900px and 601px
        itemsTablet: [600, 2], //2 items between 600 and 0;
        itemsMobile: [480, 1], // itemsMobile disabled - inherit from itemsTablet option
        pagination: false,
        navigationText: false

    });

    // Custom Navigation Events
    $("#team-carousel-next").click(function() {
        owl.trigger("owl.next");
    });
    $("#team-carousel-prev").click(function() {
        owl.trigger("owl.prev");
    });


    /* ----------------------------------------------------------- */
    /*  -- Misc
    /* ----------------------------------------------------------- */

    $(".title-bordered h2").append('<span class="line line__right"></span>')
        .prepend('<span class="line line__left"></span>');

    // Animation on scroll
    var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    if (isMobile == false) {
        $("*[data-animation]").addClass("animated");

        $(".animated").appear(function() {
            var elem = $(this);
            var animation = elem.data("animation");
            if (!elem.hasClass("visible")) {
                var animationDelay = elem.data("animation-delay");
                if (animationDelay) {

                    setTimeout(function() {
                            elem.addClass(animation + " visible");
                        },
                        animationDelay);

                } else {
                    elem.addClass(animation + " visible");
                }
            }
        });
    }

    // InfoBar
    $(".infobar-trigger").click(function() {
        $(".slider-infobar").toggleClass("active");
    });

    // Magnific Popup
    $(".magnific-img").magnificPopup({
        removalDelay: 300,
        type: "image",
        mainClass: "mfp-fade"
    });

});


// Triangles (Categories)
$(document).ready(function() {

    var titleWidth = $(".cat-title").width();
    $(".cat-title .triangle-top, .cat-title .triangle-bottom").css({
        "border-right": titleWidth / 2 + "px solid transparent"
    });
    $(".cat-title .triangle-top, .cat-title .triangle-bottom").css({
        "border-left": titleWidth / 2 + "px solid transparent"
    });
});

$(window).resize(function() {
    var titleWidthR = $(".cat-title").width();
    $(".cat-title .triangle-top, .cat-title .triangle-bottom").css({
        "border-right": titleWidthR / 2 + "px solid transparent"
    });
    $(".cat-title .triangle-top, .cat-title .triangle-bottom").css({
        "border-left": titleWidthR / 2 + "px solid transparent"
    });
});


// Triangles (Icoboxes)
$(document).ready(function() {

    var icoboxWidth = $(".icobox-holder").width();
    $(".icobox-holder .triangle-top").css({
        "border-top": icoboxWidth / 3 + "px solid rgba(255,255,255,.1)"
    });
    $(".icobox-holder .triangle-bottom").css({
        "border-bottom": icoboxWidth / 3 + "px solid rgba(0,0,0,.02)"
    });
    $(".icobox-holder .triangle-top, .icobox-holder .triangle-bottom").css({
        "border-right": icoboxWidth / 2 + "px solid transparent"
    });
    $(".icobox-holder .triangle-top, .icobox-holder .triangle-bottom").css({
        "border-left": icoboxWidth / 2 + "px solid transparent"
    });
});

$(window).resize(function() {
    var icoboxWidthR = $(".icobox-holder").width();
    $(".icobox-holder .triangle-top").css({
        "border-top": icoboxWidthR / 3 + "px solid rgba(255,255,255,.1)"
    });
    $(".icobox-holder .triangle-bottom").css({
        "border-bottom": icoboxWidthR / 3 + "px solid rgba(0,0,0,.02)"
    });
    $(".icobox-holder .triangle-top, .icobox-holder .triangle-bottom").css({
        "border-right": icoboxWidthR / 2 + "px solid transparent"
    });
    $(".icobox-holder .triangle-top, .icobox-holder .triangle-bottom").css({
        "border-left": icoboxWidthR / 2 + "px solid transparent"
    });
});