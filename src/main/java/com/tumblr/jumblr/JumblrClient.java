package com.tumblr.jumblr;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.responses.ResponseWrapper;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public final class JumblrClient {

    private final OAuthService service;
    private Token token = null;
    private String apiKey;

    /**
     * Instantiate a new Jumblr Client with no token
     * @param consumerKey The consumer key for the client
     * @param consumerSecret The consumer secret for the client
     */
    public JumblrClient(String consumerKey, String consumerSecret) {
        this.apiKey = consumerKey;
        this.service = new ServiceBuilder().
            provider(TumblrApi.class).
            apiKey(consumerKey).apiSecret(consumerSecret).
            build();        
    }
    
    /**
     * Instantiate a new Jumblr Client
     * @param consumerKey The consumer key for the client
     * @param consumerSecret The consumer secret for the client
     * @param token The token for the client
     * @param tokenSecret The token secret for the client
     */
    public JumblrClient(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this(consumerKey, consumerSecret);
        this.setToken(token, tokenSecret);
    }

    /**
     * Set the token for this client
     * @param token The token for the client
     * @param tokenSecret The token secret for the client
     */
    public void setToken(String token, String tokenSecret) {
        this.token = new Token(token, tokenSecret);
    }
    
    /**
     * Get the user info for the authenticated User
     * @return The authenticated user
     */
    public User user() {
        return this.clearGet("/user/info").getUser();
    }
    
    /**
     * Get the user dashboard for the authenticated User
     * @param options the options for the call (or null)
     * @return A List of posts
     */
    public List<Post> userDashboard(Map<String, ?> options) {
        return this.clearGet("/user/dashboard", options).getPosts();
    }
    
    public List<Post> userDashboard() {
        return this.userDashboard(null);
    }
    
    /**
     * Get the blogs the given user is following
     * @return a List of blogs
     */
    public List<Blog> userFollowing(Map<String, ?> options) {
        return this.clearGet("/user/following", options).getBlogs();
    }

    public List<Blog> userFollowing() { return this.userFollowing(null); }
    
    /**
     * Get the blog info for a given blog
     * @param blogName the Name of the blog
     * @return The Blog object for this blog
     */
    public Blog blogInfo(String blogName) {
        HashMap map = new HashMap<String, String>();
        map.put("api_key", this.apiKey);
        return this.clearGet(JumblrClient.blogPath(blogName, "/info"), map).getBlog();
    }
    
    /**
     * Get the followers for a given blog
     * @param blogName the name of the blog
     * @return the blog object for this blog
     */
    public List<User> blogFollowers(String blogName, Map<String, ?> options) {
        return this.clearGet(JumblrClient.blogPath(blogName, "/followers"), options).getUsers();
    }

    public List<User> blogFollowers(String blogName) { return this.blogFollowers(blogName, null); }
    
    /**
     * Get the public likes for a given blog
     * @param blogName the name of the blog
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> blogLikes(String blogName, Map<String, ?> options) {
        if (options == null) {
            options = new HashMap<String, String>();
        }
        Map<String, String> soptions = (Map<String, String>)options;
        soptions.put("api_key", this.apiKey);
        return this.clearGet(JumblrClient.blogPath(blogName, "/likes"), options).getLikedPosts();
    }
    
    public List<Post> blogLikes(String blogName) {
        return this.blogLikes(blogName, null);
    }
    
    /**
     * Get the posts for a given blog
     * @param blogName the name of the blog
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> blogPosts(String blogName, Map<String, ?> options) {
        if (options == null) {
            options = new HashMap<String, String>();
        }
        Map<String, String> soptions = (Map<String, String>) options;
        soptions.put("api_key", apiKey);

        String path = "/posts";
        if (options.containsKey("type")) {
            path += "/" + options.get("type").toString();
            options.remove("type");
        }
        return this.clearGet(JumblrClient.blogPath(blogName, path), options).getPosts();
    }
    
    public List<Post> blogPosts(String blogName) {
        return this.blogPosts(blogName, null);
    }
    
    /**
     * Get the queued posts for a given blog
     * @param blogName the name of the blog
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> blogQueuedPosts(String blogName, Map<String, ?> options) {
        return this.clearGet(JumblrClient.blogPath(blogName, "/posts/queue"), options).getPosts();
    }
    
    public List<Post> blogQueuedPosts(String blogName) {
        return this.blogQueuedPosts(blogName, null);
    }
    
    /**
     * Get the draft posts for a given blog
     * @param blogName the name of the blog
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> blogDraftPosts(String blogName, Map<String, ?> options) {
        return this.clearGet(JumblrClient.blogPath(blogName, "/posts/draft"), options).getPosts();
    }
    
    public List<Post> blogDraftPosts(String blogName) {
        return this.blogDraftPosts(blogName, null);
    }

    /**
     * Get the submissions for a given blog
     * @param blogName the name of the blog
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> blogSubmissions(String blogName, Map<String, ?> options) {
        return this.clearGet(JumblrClient.blogPath(blogName, "/posts/submission"), options).getPosts();
    }
    
    public List<Post> blogSubmissions(String blogName) {
        return this.blogSubmissions(blogName, null);
    }    
    
    /**
     * Get the likes for the authenticated user
     * @param options the options for this call (or null)
     * @return a List of posts
     */
    public List<Post> userLikes(Map<String, ?> options) {
        return this.clearGet("/user/likes", options).getLikedPosts();
    }
    
    public List<Post> userLikes() {
        return this.userLikes(null);
    }
    
    /**
     * Get a specific size avatar for a given blog
     * @param blogName the avatar URL of the blog
     * @param size The size requested
     * @return a string representing the URL of the avatar
     */
    public String blogAvatar(String blogName, Integer size) {
        String pathExt = size == null ? "" : "/" + size.toString();
        boolean presetVal = HttpURLConnection.getFollowRedirects();
        HttpURLConnection.setFollowRedirects(false);
        Response response = this.get(JumblrClient.blogPath(blogName, "/avatar" + pathExt));
        HttpURLConnection.setFollowRedirects(presetVal);
        if (response.getCode() == 301) {
            return response.getHeader("Location");
        } else {
            throw new JumblrException(response);
        }        
    }

    public String blogAvatar(String blogName) { return this.blogAvatar(blogName, null); }
 
    /**
     * Like a given post
     * @param postId the ID of the post to like
     * @param reblogKey The reblog key for the post
     */
    public void like(BigInteger postId, String reblogKey) {
        Map map = new HashMap<String, String>();
        map.put("id", postId.toString());
        map.put("reblog_key", reblogKey);
        this.clearPost("/user/like", map);
    }
    
    /**
     * Unlike a given post
     * @param postId the ID of the post to unlike
     * @param reblogKey The reblog key for the post
     */
    public void unlike(BigInteger postId, String reblogKey) {
        Map map = new HashMap<String, String>();
        map.put("id", postId.toString());
        map.put("reblog_key", reblogKey);
        this.clearPost("/user/unlike", map);
    }
    
    /**
     * Follow a given blog
     * @param blogName The name of the blog to follow
     */
    public void follow(String blogName) {
        Map map = new HashMap<String, String>();
        map.put("url", JumblrClient.blogUrl(blogName));
        this.clearPost("/user/follow", map);
    }
    
    /**
     * Unfollow a given blog
     * @param blogName the name of the blog to unfollow
     */
    public void unfollow(String blogName) {
        Map map = new HashMap<String, String>();
        map.put("url", JumblrClient.blogUrl(blogName));
        this.clearPost("/user/follow", map);
    }
    
    /**
     * Delete a given post
     * @param blogName the name of the blog the post is in
     * @param postId the id of the post to delete
     */
    public void postDelete(String blogName, BigInteger postId) {
        Map map = new HashMap<String, String>();
        map.put("id", postId);
        this.clearPost(JumblrClient.blogPath(blogName, "/post/delete"), map);
    }
    
    /**
     * Reblog a given post
     * @param blogName the name of the blog the post is in
     * @param postId the id of the post
     * @param reblogKey the reblog_key of the post
     * @param options Additional options (or null)
     */
    public Post postReblog(String blogName, BigInteger postId, String reblogKey, Map<String, ?> options) {
        if (options == null) {
            options = new HashMap<String, String>();
        }
        Map<String, String> soptions = (Map<String, String>)options;
        soptions.put("id", postId.toString());
        soptions.put("reblog_key", reblogKey);
        return this.clearPost(JumblrClient.blogPath(blogName, "/post/reblog"), options).getPost();
    }
    
    public Post postReblog(String blogName, BigInteger postId, String reblogKey) {
        return this.postReblog(blogName, postId, reblogKey, null);
    }
    
    /**
     **
     **
     */
    
    private ResponseWrapper clearGet(String path) {
        return this.clearGet(path, null);
    }
    
    private ResponseWrapper clearPost(String path, Map<String, ?> bodyMap) {
        Response response = this.post(path, bodyMap);
        return this.clear(response);
    }
    
    private ResponseWrapper clearGet(String path, Map<String, ?> map) {
        Response response = this.get(path, map);
        return this.clear(response);
    }
    
    private ResponseWrapper clear(Response response) {
        if (response.getCode() == 200 || response.getCode() == 201) {
            String json = response.getBody();
            try {
                Gson gson = new Gson();
                ResponseWrapper wrapper = gson.fromJson(json, ResponseWrapper.class);
                wrapper.setClient(this);
                return wrapper;
            } catch (JsonSyntaxException ex) {
                return null;
            }
        } else {
            throw new JumblrException(response);
        }
    }
    
    private Response get(String path) {
        return this.get(path, null);
    }
    
    private Response get(String path, Map<String, ?> queryParams) {
        String url = "http://api.tumblr.com/v2" + path;
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        if (queryParams != null) {
            for (String key : queryParams.keySet()) {
                request.addQuerystringParameter(key, queryParams.get(key).toString());
            }
        }
        service.signRequest(token, request);
        return request.send();
    }

    private Response post(String path, Map<String, ?> bodyMap) {
        String url = "http://api.tumblr.com/v2" + path;
        OAuthRequest request = new OAuthRequest(Verb.POST, url);
        if (bodyMap != null) {
            for (String key : bodyMap.keySet()) {
                request.addBodyParameter(key, bodyMap.get(key).toString());
            }
        }
        service.signRequest(token, request);
        return request.send();
    }
    
    private static String blogPath(String blogName, String extPath) {
        String bn = blogName.contains(".") ? blogName : blogName + ".tumblr.com";
        return "/blog/" + bn + extPath;
    }

    private static String blogUrl(String blogName) {
        return blogName.contains(".") ? blogName : blogName + ".tumblr.com";
    }

}
