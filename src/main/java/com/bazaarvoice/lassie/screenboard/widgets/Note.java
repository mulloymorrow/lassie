/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bazaarvoice.lassie.screenboard.widgets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Note is a {@link Widget} that acts as a literal note placed on the screenboard.
 * It has a background color and it has a tick mark that can be placed in a direction to
 * annotate another element on the page.
 */
public class Note extends Widget {
    @JsonProperty("title_size")
    private int _titleSize = 16;
    @JsonProperty("title")
    private boolean _titleVisible = true;
    @JsonProperty("tick_pos")
    private String _tickPosition = "50%";
    @JsonProperty("title_align")
    private Alignment _titleAlignment = Alignment.LEFT;
    @JsonProperty("tick_edge")
    private Edge _tickEdge = Edge.RIGHT;
    @JsonProperty("text_align")
    private Alignment _textAlignment = Alignment.LEFT;
    @JsonProperty("title_text")
    private String _title = "Title";
    @JsonProperty("bgcolor")
    private BackgroundColor _backgroundColor = BackgroundColor.YELLOW;
    @JsonProperty("html")
    private String _text = "body";
    @JsonProperty("font_size")
    private String _fontSize = "14";
    @JsonProperty("tick")
    private boolean _tickVisible = true;
    @JsonProperty("auto_refresh")
    private boolean _autoRefresh = false;

    /** The Edge enum serves to denote which edge of the {@link Note} widget that the "tick" will exist on. */
    public enum Edge {
        LEFT("left"), TOP("top"), RIGHT("right"), BOTTOM("bottom");

        private final String _name;

        /**
         * The constructor that sets the name of the enum as referenced in the datadog API.
         *
         * @param name The name of the eunm.
         */
        private Edge(String name) {
            _name = name;
        }

        /**
         * The getter for the name of the Edge enum.
         *
         * @return The name of the Edge enum.
         */
        @JsonValue
        public String getName() {
            return _name;
        }

        /**
         * Getter for the Edge with a given name.
         *
         * @param name The Edge's name.
         * @return The Edge matching the name.
         */
        @JsonCreator
        public static Edge fromName(String name) {
            checkNotNull(name);
            return Edge.valueOf(name.toUpperCase());
        }
    }

    /** BackgroundColor enum which is used by the {@link Note} widget. */
    public enum BackgroundColor {
        YELLOW("yellow"), BLUE("blue"), PINK("pink"), GRAY("gray"), WHITE("white"), RED("red"), GREEN("green");

        private final String _name;

        /**
         * Constructor that sets the name of the enum as it is documented in the datadog API.
         *
         * @param name The name of the enum.
         */
        private BackgroundColor(String name) {
            _name = name;
        }

        /**
         * The getter for the name of the enum as it is documented in the datadog API.
         *
         * @return The name of the BackgroundColor.
         */
        @JsonValue
        public String getName() {
            return _name;
        }

        /**
         * The name of the expected enum as it is documented in the datadog API.
         *
         * @param name The name of the expected BackgroundColor.
         * @return The expected BackgroundColor.
         */
        @JsonCreator
        public static BackgroundColor fromName(String name) {
            checkNotNull(name);
            return BackgroundColor.valueOf(name.toUpperCase());
        }
    }

    /**
     * The constructor for the Note that takes in a location and dimension.
     *
     * @param location   The location of the Note.
     * @param dimensions The dimension of the Note.
     */
    public Note(Location location, Dimensions dimensions) {
        super(location, dimensions);
    }

    /**
     * The constructor for the EventStream that takes in a x / y and width / height.
     *
     * @param x      The Note's x value.
     * @param y      The Note's y value.
     * @param width  The Note's width.
     * @param height The Note's height.
     */
    public Note(int x, int y, int width, int height) {
        this(new Location(x, y), new Dimensions(width, height));
    }

    /**
     * Private constructor used for deserialization.
     * Set in the top left corner of the board with the default dimensions.
     */
    public Note() {
        this(0, 0, 30, 15);
    }

    @JsonIgnore
    public int getTitleSize() {
        return _titleSize;
    }

    public void setTitleSize(int titleSize) {
        checkArgument(titleSize > 0, "titleSize is less then one");
        _titleSize = titleSize;
    }

    @JsonIgnore
    public String getTickPosition() {
        return _tickPosition;
    }

    public void setTickPosition(String tickPosition) {
        _tickPosition = checkNotNull(tickPosition, "tickPosition is null");
    }

    @JsonIgnore
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = checkNotNull(title, "titleText is null");
    }

    @JsonIgnore
    public BackgroundColor getBackgroundColor() {
        return _backgroundColor;
    }

    public void setBackgroundColor(BackgroundColor backgroundColor) {
        _backgroundColor = checkNotNull(backgroundColor, "backgroundColor is null");
    }

    @JsonIgnore
    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = checkNotNull(text, "text is null");
    }

    @JsonIgnore
    public String getFontSize() {
        return _fontSize;
    }

    public void setFontSize(String fontSize) {
        _fontSize = checkNotNull(fontSize, "fontSize is null");
    }

    @JsonIgnore
    public boolean isTitleVisible() {
        return _titleVisible;
    }

    public void setTitleVisible(boolean titleVisible) {
        _titleVisible = titleVisible;
    }

    @JsonIgnore
    public Alignment getTitleAlignment() {
        return _titleAlignment;
    }

    public void setTitleAlignment(Alignment titleAlignment) {
        _titleAlignment = checkNotNull(titleAlignment, "titleAlignment is null");
    }

    /**
     * The getter for what {@link Edge} the tick (pointer) exists
     *
     * @return The side that the tick will render on.
     */
    @JsonIgnore
    public Edge getTickEdge() {
        return _tickEdge;
    }

    /**
     * The getter for what {@link Edge} the tick (pointer) exists
     *
     * @param tickEdge The side that the tick will render on.
     */
    public void setTickEdge(Edge tickEdge) {
        _tickEdge = checkNotNull(tickEdge, "tickEdge is null");
    }

    @JsonIgnore
    public Alignment getTextAlignment() {
        return _textAlignment;
    }

    public void setTextAlignment(Alignment textAlignment) {
        _textAlignment = checkNotNull(textAlignment, "textAlignment is null");
    }

    @JsonIgnore
    public boolean isTickVisible() {
        return _tickVisible;
    }

    public void setTickVisible(boolean tickVisible) {
        _tickVisible = tickVisible;
    }

    @JsonIgnore
    public boolean isAutoRefresh() {
        return _autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        _autoRefresh = autoRefresh;
    }

    @JsonIgnore
    public String toString() {
        return "Note[" +
                "title=" + getTitle() +
                ", titleAlignment=" + getTitleAlignment() +
                ", titleSize=" + getTitleSize() +
                ", titleVisible=" + isTitleVisible() +
                ", text=" + getText() +
                ", textAlignment=" + getTextAlignment() +
                ", tickEdge=" + getTickEdge() +
                ", fontSize=" + getFontSize() +
                ", tickVisible=" + isTickVisible() +
                ", autoRefresh=" + isAutoRefresh() +
                "]";
    }
}
