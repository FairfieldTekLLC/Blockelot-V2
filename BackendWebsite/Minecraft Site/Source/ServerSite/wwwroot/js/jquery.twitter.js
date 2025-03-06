//JQuery Twitter Feed. Coded by Tom Elliott @ www.webdevdoor.com (2013) based on https://twitter.com/javascripts/blogger.js
//Requires JSON output from authenticating script: http://www.webdevdoor.com/php/authenticating-twitter-feed-timeline-oauth/

jQuery(function($) {
    var displaylimit = 3;
    var twitterprofile = "OliaGozha";
    var screenname = "Olia Gozha";
    var showdirecttweets = false;
    var showretweets = true;
    var showtweetlinks = true;
    var showprofilepic = true;
    var showtweetactions = true;
    var showretweetindicator = false;

    var headerHTML = "";
    var loadingHTML = "";
    headerHTML = "";
    loadingHTML += '<div id="loading-container" class="loading-container"><i class="fa fa-spinner fa-spin"></i></div>';

    $(".twitter-feed").html(headerHTML + loadingHTML);

    $.getJSON("get-tweets1.1.php",
        function(feeds) {
            // alert(feeds);
            var feedHTML = "";
            var displayCounter = 1;
            for (var i = 0; i < feeds.length; i++) {
                var tweetscreenname = feeds[i].user.name;
                var tweetusername = feeds[i].user.screen_name;
                var profileimage = feeds[i].user.profile_image_url_https;
                var status = feeds[i].text;
                var isaretweet = false;
                var isdirect = false;
                var tweetid = feeds[i].id_str;

                //If the tweet has been retweeted, get the profile pic of the tweeter
                if (typeof feeds[i].retweeted_status != "undefined") {
                    profileimage = feeds[i].retweeted_status.user.profile_image_url_https;
                    tweetscreenname = feeds[i].retweeted_status.user.name;
                    tweetusername = feeds[i].retweeted_status.user.screen_name;
                    tweetid = feeds[i].retweeted_status.id_str;
                    status = feeds[i].retweeted_status.text;
                    isaretweet = true;
                };


                //Check to see if the tweet is a direct message
                if (feeds[i].text.substr(0, 1) == "@") {
                    isdirect = true;
                }

                //console.log(feeds[i]);

                //Generate twitter feed HTML based on selected options
                if (((showretweets == true) || ((isaretweet == false) && (showretweets == false))) &&
                    ((showdirecttweets == true) || ((showdirecttweets == false) && (isdirect == false)))) {
                    if ((feeds[i].text.length > 1) && (displayCounter <= displaylimit)) {
                        if (showtweetlinks == true) {
                            status = addlinks(status);
                        }

                        if (displayCounter == 1) {
                            feedHTML += headerHTML;
                        }

                        feedHTML += '<div class="twitter-article-col"><div class="twitter-article">';
                        feedHTML += '<a class="btn btn-primary btn-sm btn-twitter" href="https://twitter.com/' +
                            tweetusername +
                            '" target="_blank"><i class="fa fa-twitter"></i><span class="inner-txt"></span></a>';
                        feedHTML += '<div class="twitter-pic"><a href="https://twitter.com/' +
                            tweetusername +
                            '" target="_blank"><img src="' +
                            profileimage +
                            '"images/twitter-feed-icon.png" width="50" height="50" alt="twitter icon" /></a></div>';
                        feedHTML +=
                            '<div class="twitter-meta"><span class="tweetprofilelink"><span class="twitter-user-name">' +
                            tweetscreenname +
                            '</span> <a href="https://twitter.com/' +
                            tweetusername +
                            '" target="_blank">@' +
                            tweetusername +
                            "</a></span></div>";
                        feedHTML += '<div class="twitter-text"><p>' + status + "</p></div>";

                        if ((isaretweet == true) && (showretweetindicator == true)) {
                            feedHTML += '<div class="retweet-indicator"></div>';
                        }

                        feedHTML += '<div class="twitter-date"><span class="tweet-time"><a href="https://twitter.com/' +
                            tweetusername +
                            "/status/" +
                            tweetid +
                            '" target="_blank">' +
                            relative_time(feeds[i].created_at) +
                            "</a></span></div>";

                        if (showtweetactions == true) {
                            feedHTML +=
                                '<div class="twitter-actions"><div class="intent intent-reply"><a href="https://twitter.com/intent/tweet?in_reply_to=' +
                                tweetid +
                                '" title="Reply"><i class="fa fa-reply"></i><span class="txt-wrap">Reply</span></a></div><div class="intent intent-retweet"><a href="https://twitter.com/intent/retweet?tweet_id=' +
                                tweetid +
                                '" title="Retweet"><i class="fa fa-retweet"></i><span class="txt-wrap">Retweet</span></a></div><div class="intent intent-fave"><a href="https://twitter.com/intent/favorite?tweet_id=' +
                                tweetid +
                                '" title="Favorite"><i class="fa fa-star"></i><span class="txt-wrap">Favorite</span></a></div></div>';
                        }

                        feedHTML += '<div class="clear"></div></div></div>';
                        displayCounter++;
                    }
                }
            }

            $(".twitter-feed").html(feedHTML);


            //Add new window for action clicks
            if (showtweetactions == true) {

                $(".twitter-actions a").click(function() {
                    var url = $(this).attr("href");
                    window.open(url, "tweet action window", "width=580,height=500");
                    return false;
                });
            }


        }).error(function(jqXHR, textStatus, errorThrown) {
        if (errorThrown == "Not Found") {
            errorThrown = "not found: tweets php script";
        }
        // alert(textStatus + " - " + errorThrown);
    });


    //Function modified from Stack Overflow
    function addlinks(data) {
        //Add link to all http:// links within tweets
        data = data.replace(/((https?|s?ftp|ssh)\:\/\/[^"\s\<\>]*[^.,;'">\:\s\<\>\)\]\!])/g,
            function(url) {
                return '<a href="' + url + '" >' + url + "</a>";
            });

        //Add link to @usernames used within tweets
        data = data.replace(/\B@([_a-z0-9]+)/ig,
            function(reply) {
                return '<a href="http://twitter.com/' +
                    reply.substring(1) +
                    '" style="font-weight:lighter;" target="_blank">' +
                    reply.charAt(0) +
                    reply.substring(1) +
                    "</a>";
            });
        //Add link to #hastags used within tweets
        data = data.replace(/\B#([_a-z0-9]+)/ig,
            function(reply) {
                return '<a href="https://twitter.com/search?q=' +
                    reply.substring(1) +
                    '" style="font-weight:lighter;" target="_blank">' +
                    reply.charAt(0) +
                    reply.substring(1) +
                    "</a>";
            });
        return data;
    }


    function relative_time(time_value) {
        var values = time_value.split(" ");
        time_value = values[1] + " " + values[2] + ", " + values[5] + " " + values[3];
        var parsed_date = Date.parse(time_value);
        var relative_to = (arguments.length > 1) ? arguments[1] : new Date();
        var delta = parseInt((relative_to.getTime() - parsed_date) / 1000);
        var shortdate = time_value.substr(4, 2) + " " + time_value.substr(0, 3);
        delta = delta + (relative_to.getTimezoneOffset() * 60);

        if (delta < 60) {
            return "1m";
        } else if (delta < 120) {
            return "1m";
        } else if (delta < (60 * 60)) {
            return (parseInt(delta / 60)).toString() + "m";
        } else if (delta < (120 * 60)) {
            return "1h";
        } else if (delta < (24 * 60 * 60)) {
            return (parseInt(delta / 3600)).toString() + "h";
        } else if (delta < (48 * 60 * 60)) {
            //return '1 day';
            return shortdate;
        } else {
            return shortdate;
        }
    }

});