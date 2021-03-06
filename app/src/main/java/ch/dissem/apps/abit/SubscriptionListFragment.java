/*
 * Copyright 2015 Christian Basler
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

package ch.dissem.apps.abit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.dissem.bitmessage.entity.BitmessageAddress;
import ch.dissem.bitmessage.entity.valueobject.Label;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chris on 06.09.15.
 */
public class SubscriptionListFragment extends AbstractItemListFragment<BitmessageAddress> {
    @Override
    public void onResume() {
        super.onResume();

        updateList();
    }

    public void updateList() {
        List<BitmessageAddress> addresses = bmc.addresses().getContacts();
        Collections.sort(addresses, new Comparator<BitmessageAddress>() {
            /**
             * Yields the following order:
             * <ol>
             *     <li>Subscribed addresses come first</li>
             *     <li>Addresses with Aliases (alphabetically)</li>
             *     <li>Addresses (alphabetically)</li>
             * </ol>
             */
            @Override
            public int compare(BitmessageAddress lhs, BitmessageAddress rhs) {
                if (lhs.isSubscribed() == rhs.isSubscribed()) {
                    if (lhs.getAlias() != null) {
                        if (rhs.getAlias() != null) {
                            return lhs.getAlias().compareTo(rhs.getAlias());
                        } else {
                            return -1;
                        }
                    } else if (rhs.getAlias() != null) {
                        return 1;
                    } else {
                        return lhs.getAddress().compareTo(rhs.getAddress());
                    }
                }
                if (lhs.isSubscribed()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        setListAdapter(new ArrayAdapter<BitmessageAddress>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                addresses) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.subscription_row, null, false);
                }
                BitmessageAddress item = getItem(position);
                ((ImageView) convertView.findViewById(R.id.avatar)).setImageDrawable(new Identicon(item));
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(item.toString());
                TextView streamNumber = (TextView) convertView.findViewById(R.id.stream_number);
                streamNumber.setText(getContext().getString(R.string.stream_number, item.getStream()));
                convertView.findViewById(R.id.subscribed).setVisibility(item.isSubscribed() ? View.VISIBLE : View.INVISIBLE);
                return convertView;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscribtions, container, false);

        return rootView;
    }

    @Override
    void updateList(Label label) {
        updateList();
    }
}
