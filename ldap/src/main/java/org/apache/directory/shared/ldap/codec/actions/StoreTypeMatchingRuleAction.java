/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.codec.actions;


import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.message.internal.InternalSearchRequest;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a type matching rule
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreTypeMatchingRuleAction extends GrammarAction
{
    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( StoreTypeMatchingRuleAction.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = log.isDebugEnabled();


    public StoreTypeMatchingRuleAction()
    {
        super( "Store matching type Value" );
    }


    /**
     * The initialization action
     */
    public void action( IAsn1Container container ) throws DecoderException
    {
        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer ) container;
        InternalSearchRequest searchRequest = ldapMessageContainer.getSearchRequest();

        TLV tlv = ldapMessageContainer.getCurrentTLV();

        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04022 );
            log.error( msg );
            throw new DecoderException( msg );
        }
        else
        {
            // Store the value.
            ExtensibleMatchFilter extensibleMatchFilter = ( ExtensibleMatchFilter ) ( ( SearchRequestImpl ) searchRequest )
                .getTerminalFilter();

            String type = StringTools.utf8ToString( tlv.getValue().getData() );
            extensibleMatchFilter.setType( type );

            if ( IS_DEBUG )
            {
                log.debug( "Stored a type matching rule : {}", type );
            }
        }
    }
}
