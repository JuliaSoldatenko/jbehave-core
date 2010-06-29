package org.jbehave.core.io;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import java.net.MalformedURLException;
import java.util.List;

import org.jbehave.core.Embeddable;
import org.jbehave.core.embedder.EmbedderClassLoader;
import org.jbehave.core.io.stories.MyStory;
import org.junit.Test;

public class StoryFinderBehaviour {

    @Test
    public void shouldFindPaths() {
        StoryFinder finder = new StoryFinder();
        List<String> storyPaths = finder.findPaths("src/behaviour/java", asList("**/stories/*_story"), asList(""));
        assertThat(storyPaths.size(), equalTo(4));
        assertThat(storyPaths, hasItem(containsString("/")));
        assertThat(storyPaths, hasItem(not(startsWith("/"))));
        assertThat(storyPaths, hasItem(startsWith("org/jbehave/core/io/stories")));
        assertThat(storyPaths, hasItem(endsWith("_story")));
    }

    @Test
    public void shouldFindPathsAndPrefixThem() {
        StoryFinder finder = new StoryFinder();
        List<String> storyPaths = finder.findPaths("src/behaviour/java", asList("**/stories/*_story"), asList(""), "file:");
        assertThat(storyPaths.size(), equalTo(4));
        assertThat(storyPaths, hasItem(containsString("/")));
        assertThat(storyPaths, hasItem(not(startsWith("/"))));
        assertThat(storyPaths, hasItem(startsWith("file:org/jbehave/core/io/stories")));
        assertThat(storyPaths, hasItem(endsWith("_story")));
    }

    @Test
    public void shouldFindClassNames() {
        StoryFinder finder = new StoryFinder();
        List<String> classNames = finder.findClassNames("src/behaviour/java", asList("**/stories/*.java"), asList(""));
        assertThat(classNames.size(), equalTo(3));
        assertThat(classNames, hasItem(not(containsString("/"))));
        assertThat(classNames, hasItem(not(endsWith(".java"))));
        assertThat(classNames, hasItem(startsWith("org.jbehave.core.io.stories")));
    }

    @Test
    public void shouldFindEmbeddablesOnlyForNonAbstractClasses() throws MalformedURLException {
        StoryFinder finder = new StoryFinder();
        EmbedderClassLoader classLoader = new EmbedderClassLoader(asList("target/test-classes"));
        List<Embeddable> embeddables = finder.findEmbeddables("src/behaviour/java", asList("**/stories/*.java"), asList(""), classLoader);
        assertThat(embeddables.size(), equalTo(1));
        assertThat(embeddables.iterator().next(), instanceOf(MyStory.class));
    }

    @Test
    public void shouldNormalisePaths(){
        StoryFinder finder = new StoryFinder();
        assertThat(finder.normalise(asList("path/to/a.story", "/path/to/a.story")), equalTo(asList("path/to/a.story", "/path/to/a.story")));
        assertThat(finder.normalise(asList("path\\to\\a.story", "\\path\\to\\a.story")), equalTo(asList("path/to/a.story", "/path/to/a.story")));
    }
    
    @Test
    public void shouldIgnoreNullFiltersWhenFindingPaths() {
        StoryFinder finder = new StoryFinder();
        assertThat(finder.findPaths("src/behaviour/java", null, null).size(), greaterThan(0));
    }

    @Test
    public void shouldReturnEmptyListForInexistentBasedir() {
        StoryFinder finder = new StoryFinder();
        assertThat(finder.findPaths("/inexistent", asList(""), asList("")).size(), equalTo(0));
    }
    
}