/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions and Fabricio Colombo
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 * Fabricio Colombo (fabricio.colombo.mva@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.squid.text.delphi;

public class SingleLineCommentHandler extends LineContextHandler {

    private StringBuilder comment;

    private final String commentStartTag;
    private final String commentNotStartTag;

    public SingleLineCommentHandler(String commentStartTag) {
        this(commentStartTag, null);
    }

    public SingleLineCommentHandler(String commentStartTag, String commentNotStartTag) {
        this.commentStartTag = commentStartTag;
        this.commentNotStartTag = commentNotStartTag;
    }

    @Override
    boolean matchToEnd(Line line, StringBuilder pendingLine) {
        if (comment == null) {
            throw new IllegalStateException("Method doContextBegin(StringBuilder pendingLine) has not been called.");
        }
        comment.append(getLastCharacter(pendingLine));
        line.isThereCode();

        return false;
    }

    @Override
    boolean matchToBegin(Line line, StringBuilder pendingLine) {
        boolean doContextBegin = matchEndOfString(pendingLine, commentStartTag)
                && (commentNotStartTag == null || !matchEndOfString(pendingLine, commentNotStartTag));
        if (doContextBegin) {
            comment = new StringBuilder(commentStartTag);
        }
        return doContextBegin;
    }

    @Override
    boolean matchWithEndOfLine(Line line, StringBuilder pendingLine) {
        String newComment = "";
        String oldComment = line.getComment();

        if (oldComment != null) {
            newComment = oldComment.concat(comment.toString());
        } else {
            newComment = comment.toString();
        }

        line.setComment(newComment);
        comment = null;
        return true;
    }
}
